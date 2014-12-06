package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.web.domain.*;
import net.dorokhov.pony.web.domain.command.CreateUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateCurrentUserCommand;
import net.dorokhov.pony.web.domain.command.UpdateUserCommand;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.security.UserTokenProvider;
import net.dorokhov.pony.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class ApiController {

	private ResponseBuilder responseBuilder;

	private UserTokenProvider userTokenProvider;

	private DtoConverter dtoConverter;

	private InstallationServiceFacade installationServiceFacade;

	private UserServiceFacade userServiceFacade;

	private ScanServiceFacade scanServiceFacade;

	private SongServiceFacade songServiceFacade;

	@Autowired
	public void setResponseBuilder(ResponseBuilder aResponseBuilder) {
		responseBuilder = aResponseBuilder;
	}

	@Autowired
	public void setUserTokenProvider(UserTokenProvider aUserTokenProvider) {
		userTokenProvider = aUserTokenProvider;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
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

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<Void> logout(ServletRequest aRequest) {

		userServiceFacade.logout(dtoConverter.userTokenToDto(userTokenProvider.getToken(aRequest)));

		return responseBuilder.build();
	}

	@RequestMapping(value = "/currentUser", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<UserDto> getCurrentUser() {
		return responseBuilder.build(userServiceFacade.getAuthenticatedUser());
	}

	@RequestMapping(value = "/currentUser", method = RequestMethod.PUT)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<UserDto> saveUser(@Valid @RequestParam("user") UpdateCurrentUserCommand aCommand) {
		return responseBuilder.build(userServiceFacade.updateAuthenticatedUser(aCommand));
	}

	@RequestMapping(value = "/artists", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<List<ArtistDto>> getArtists() {
		return responseBuilder.build(songServiceFacade.getArtists());
	}

	@RequestMapping(value = "/artists/{artistIdOrName}", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<ArtistSongsDto> getArtist(@PathVariable("artistIdOrName") String aArtistIdOrName) {

		ArtistSongsDto artist = songServiceFacade.getArtistSongs(aArtistIdOrName);

		if (artist == null) {
			throw new ObjectNotFoundException(aArtistIdOrName, "artistNotFound", "Artist [" + aArtistIdOrName + "] could not be found.");
		}

		return responseBuilder.build(artist);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<SearchDto> search(@RequestParam("query") String aQuery) {
		return responseBuilder.build(songServiceFacade.search(aQuery));
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<List<UserDto>> getUsers() {
		return responseBuilder.build(userServiceFacade.getAll());
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<UserDto> getUser(@PathVariable("id") Long aId) {
		return responseBuilder.build(userServiceFacade.getById(aId));
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<UserDto> createUser(@Valid @RequestParam("user") CreateUserCommand aCommand) {
		return responseBuilder.build(userServiceFacade.create(aCommand));
	}

	@RequestMapping(value = "/users", method = RequestMethod.PUT)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<UserDto> updateUser(@Valid @RequestParam("user") UpdateUserCommand aCommand) {
		return responseBuilder.build(userServiceFacade.update(aCommand));
	}

	@RequestMapping(value = "/scanJobs", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<ListDto<ScanJobDto>> getScanJobs(@RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
														@RequestParam(value = "pageSize", defaultValue = "10") int aPageSize) {
		return responseBuilder.build(scanServiceFacade.getScanJobs(aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/scanJobs/{id}", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<ScanJobDto> getScanJob(@PathVariable("id") Long aId) {

		ScanJobDto job = scanServiceFacade.getScanJob(aId);

		if (job == null) {
			throw new ObjectNotFoundException(aId, "scanJobNotFound", "Scan job [" + aId + "] could not be found.");
		}

		return responseBuilder.build(job);
	}

	@RequestMapping(value = "/scanJobs", method = RequestMethod.POST)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<ScanJobDto> startScanJob() {
		return responseBuilder.build(scanServiceFacade.startScanJob());
	}

	@RequestMapping(value = "/scanResults", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<ListDto<ScanResultDto>> getScanResults(@RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
															  @RequestParam(value = "pageSize", defaultValue = "10") int aPageSize) {
		return responseBuilder.build(scanServiceFacade.getScanResults(aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/scanResults/{id}", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<ScanResultDto> getScanResult(@PathVariable("id") Long aId) {

		ScanResultDto result = scanServiceFacade.getScanResult(aId);

		if (result == null) {
			throw new ObjectNotFoundException(aId, "scanResultNotFound", "Scan result [" + aId + "] could not be found.");
		}

		return responseBuilder.build(result);
	}

	@RequestMapping(value = "/scanStatus", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<ScanStatusDto> getScanStatus() {
		return responseBuilder.build(scanServiceFacade.getScanStatus());
	}

}
