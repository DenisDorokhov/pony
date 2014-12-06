package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.web.domain.*;
import net.dorokhov.pony.web.domain.command.SaveCurrentUserCommand;
import net.dorokhov.pony.web.domain.command.SaveUserCommand;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class ApiController {

	private ResponseBuilder responseBuilder;

	private InstallationServiceFacade installationServiceFacade;

	private UserServiceFacade userServiceFacade;

	private ScanServiceFacade scanServiceFacade;

	private SongServiceFacade songServiceFacade;

	@Autowired
	public void setResponseBuilder(ResponseBuilder aResponseBuilder) {
		responseBuilder = aResponseBuilder;
	}

	@Autowired
	public void setInstallationServiceFacade(InstallationServiceFacade aInstallationServiceFacade) {
		installationServiceFacade = aInstallationServiceFacade;
	}

	@Autowired
	public void setUserServiceFacade(UserServiceFacade aUserServiceFacade) {
		userServiceFacade = aUserServiceFacade;
	}

	@Autowired
	public void setScanServiceFacade(ScanServiceFacade aScanServiceFacade) {
		scanServiceFacade = aScanServiceFacade;
	}

	@Autowired
	public void setSongServiceFacade(SongServiceFacade aSongServiceFacade) {
		songServiceFacade = aSongServiceFacade;
	}

	@RequestMapping(value = "/installation", method = RequestMethod.GET)
	public ResponseDto<InstallationDto> getInstallation() {
		return responseBuilder.build(installationServiceFacade.getInstallation());
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseDto<UserTokenDto> authenticate(@RequestParam("email") String aEmail, @RequestParam("password") String aPassword) {
		return responseBuilder.build(userServiceFacade.authenticate(aEmail, aPassword));
	}

	@RequestMapping(value = "/currentUser", method = RequestMethod.GET)
	@PreAuthorize("hasRole('user')")
	public ResponseDto<UserDto> getCurrentUser() {
		return responseBuilder.build(userServiceFacade.getAuthenticatedUser());
	}

	@RequestMapping(value = "/currentUser", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('user')")
	public ResponseDto<UserDto> saveUser(@RequestParam("user") SaveCurrentUserCommand aSaveCurrentUserCommand) {
		return responseBuilder.build(userServiceFacade.updateAuthenticatedUser(aSaveCurrentUserCommand));
	}

	@RequestMapping(value = "/artists", method = RequestMethod.GET)
	@PreAuthorize("hasRole('user')")
	public ResponseDto<List<ArtistDto>> getArtists() {
		return responseBuilder.build(songServiceFacade.getArtists());
	}

	@RequestMapping(value = "/artists/{artistIdOrName}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('user')")
	public ResponseDto<ArtistSongsDto> getArtist(@PathVariable("artistIdOrName") String aArtistIdOrName) {

		ArtistSongsDto artist = songServiceFacade.getArtistSongs(aArtistIdOrName);

		if (artist == null) {
			throw new ObjectNotFoundException(aArtistIdOrName, "artistNotFound", "Artist [" + aArtistIdOrName + "] could not be found.");
		}

		return responseBuilder.build(artist);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@PreAuthorize("hasRole('user')")
	public ResponseDto<SearchDto> search(@RequestParam("query") String aQuery) {
		return responseBuilder.build(songServiceFacade.search(aQuery));
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<List<UserDto>> getUsers() {
		return responseBuilder.build(userServiceFacade.getAll());
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<UserDto> getUser(@PathVariable("id") Long aId) {
		return responseBuilder.build(userServiceFacade.getById(aId));
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<UserDto> createUser(@RequestParam("user") SaveUserCommand aSaveUserCommand) {
		return responseBuilder.build(userServiceFacade.create(aSaveUserCommand));
	}

	@RequestMapping(value = "/users", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<UserDto> updateUser(@RequestParam("user") SaveUserCommand aSaveUserCommand) {
		return responseBuilder.build(userServiceFacade.update(aSaveUserCommand));
	}

	@RequestMapping(value = "/scanJobs", method = RequestMethod.GET)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<ListDto<ScanJobDto>> getScanJobs(@RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
														@RequestParam(value = "pageSize", defaultValue = "10") int aPageSize) {
		return responseBuilder.build(scanServiceFacade.getScanJobs(aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/scanJobs/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<ScanJobDto> getScanJob(@PathVariable("id") Long aId) {

		ScanJobDto job = scanServiceFacade.getScanJob(aId);

		if (job == null) {
			throw new ObjectNotFoundException(aId, "scanJobNotFound", "Scan job [" + aId + "] could not be found.");
		}

		return responseBuilder.build(job);
	}

	@RequestMapping(value = "/scanJobs", method = RequestMethod.POST)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<ScanJobDto> startScanJob() {
		return responseBuilder.build(scanServiceFacade.startScanJob());
	}

	@RequestMapping(value = "/scanResults", method = RequestMethod.GET)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<ListDto<ScanResultDto>> getScanResults(@RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
															  @RequestParam(value = "pageSize", defaultValue = "10") int aPageSize) {
		return responseBuilder.build(scanServiceFacade.getScanResults(aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/scanResults/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<ScanResultDto> getScanResult(@PathVariable("id") Long aId) {

		ScanResultDto result = scanServiceFacade.getScanResult(aId);

		if (result == null) {
			throw new ObjectNotFoundException(aId, "scanResultNotFound", "Scan result [" + aId + "] could not be found.");
		}

		return responseBuilder.build(result);
	}

	@RequestMapping(value = "/scanStatus", method = RequestMethod.GET)
	@PreAuthorize("hasRole('admin')")
	public ResponseDto<ScanStatusDto> getScanStatus() {
		return responseBuilder.build(scanServiceFacade.getScanStatus());
	}

}
