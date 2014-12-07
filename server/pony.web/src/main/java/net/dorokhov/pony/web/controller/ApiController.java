package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.core.user.exception.*;
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

	private ConfigServiceFacade configServiceFacade;

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

	@Autowired
	public void setConfigServiceFacade(ConfigServiceFacade aConfigServiceFacade) {
		configServiceFacade = aConfigServiceFacade;
	}

	@RequestMapping(value = "/installation", method = RequestMethod.GET)
	public ResponseDto<InstallationDto> getInstallation() {
		return responseBuilder.build(installationServiceFacade.getInstallation());
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseDto<UserTokenDto> authenticate(@RequestParam("email") String aEmail, @RequestParam("password") String aPassword) throws InvalidCredentialsException {
		return responseBuilder.build(userServiceFacade.authenticate(aEmail, aPassword));
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<Void> logout(ServletRequest aRequest) throws InvalidTokenException {

		userServiceFacade.logout(dtoConverter.userTokenToDto(userTokenProvider.getToken(aRequest)));

		return responseBuilder.build();
	}

	@RequestMapping(value = "/currentUser", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<UserDto> getCurrentUser() throws NotAuthenticatedException {
		return responseBuilder.build(userServiceFacade.getAuthenticatedUser());
	}

	@RequestMapping(value = "/currentUser", method = RequestMethod.PUT)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<UserDto> updateCurrentUser(@Valid @RequestParam("user") UpdateCurrentUserCommand aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException {

		return responseBuilder.build(userServiceFacade.updateAuthenticatedUser(aCommand));
	}

	@RequestMapping(value = "/artists", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<List<ArtistDto>> getArtists() {
		return responseBuilder.build(songServiceFacade.getArtists());
	}

	@RequestMapping(value = "/artists/{artistIdOrName}", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.USER)
	public ResponseDto<ArtistSongsDto> getArtist(@PathVariable("artistIdOrName") String aArtistIdOrName) throws ObjectNotFoundException {

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
	public ResponseDto<UserDto> createUser(@Valid @RequestParam("user") CreateUserCommand aCommand) throws UserExistsException {
		return responseBuilder.build(userServiceFacade.create(aCommand));
	}

	@RequestMapping(value = "/users", method = RequestMethod.PUT)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<UserDto> updateUser(@Valid @RequestParam("user") UpdateUserCommand aCommand) throws UserNotFoundException, UserExistsException {
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
	public ResponseDto<ScanJobDto> getScanJob(@PathVariable("id") Long aId) throws ObjectNotFoundException {

		ScanJobDto job = scanServiceFacade.getScanJob(aId);

		if (job == null) {
			throw new ObjectNotFoundException(aId, "scanJobNotFound", "Scan job [" + aId + "] could not be found.");
		}

		return responseBuilder.build(job);
	}

	@RequestMapping(value = "/scanJobs", method = RequestMethod.POST)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<ScanJobDto> startScanJob() throws LibraryNotDefinedException {
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
	public ResponseDto<ScanResultDto> getScanResult(@PathVariable("id") Long aId) throws ObjectNotFoundException {

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

	@RequestMapping(value = "/config", method = RequestMethod.GET)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<ConfigDto> getConfig() {
		return responseBuilder.build(configServiceFacade.get());
	}

	@RequestMapping(value = "/config", method = RequestMethod.PUT)
	@RolesAllowed(RoleDto.Values.ADMIN)
	public ResponseDto<ConfigDto> saveConfig(@Valid @RequestParam("config") ConfigDto aConfig) {
		return responseBuilder.build(configServiceFacade.save(aConfig));
	}

}
