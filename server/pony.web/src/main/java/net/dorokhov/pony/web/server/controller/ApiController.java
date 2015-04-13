package net.dorokhov.pony.web.server.controller;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.server.exception.ArtworkUploadFormatException;
import net.dorokhov.pony.web.server.exception.ArtworkUploadNotFoundException;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.server.security.UserTokenReader;
import net.dorokhov.pony.web.server.service.*;
import net.dorokhov.pony.web.shared.*;
import net.dorokhov.pony.web.shared.command.CreateUserCommandDto;
import net.dorokhov.pony.web.shared.command.ScanEditCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateCurrentUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateUserCommandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class ApiController {

	private ResponseBuilder responseBuilder;

	private UserTokenReader userTokenReader;

	private InstallationServiceFacade installationServiceFacade;

	private UserServiceFacade userServiceFacade;

	private SongServiceFacade songServiceFacade;

	private ConfigServiceFacade configServiceFacade;

	private LogServiceFacade logServiceFacade;

	private ScanServiceFacade scanServiceFacade;

	private UploadService uploadService;

	@Autowired
	public void setResponseBuilder(ResponseBuilder aResponseBuilder) {
		responseBuilder = aResponseBuilder;
	}

	@Autowired
	public void setUserTokenReader(UserTokenReader aUserTokenReader) {
		userTokenReader = aUserTokenReader;
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
	public void setSongServiceFacade(SongServiceFacade aSongServiceFacade) {
		songServiceFacade = aSongServiceFacade;
	}

	@Autowired
	public void setConfigServiceFacade(ConfigServiceFacade aConfigServiceFacade) {
		configServiceFacade = aConfigServiceFacade;
	}

	@Autowired
	public void setLogServiceFacade(LogServiceFacade aLogServiceFacade) {
		logServiceFacade = aLogServiceFacade;
	}

	@Autowired
	public void setScanServiceFacade(ScanServiceFacade aScanServiceFacade) {
		scanServiceFacade = aScanServiceFacade;
	}

	@Autowired
	public void setUploadService(UploadService aUploadService) {
		uploadService = aUploadService;
	}

	@RequestMapping(value = "/installation", method = RequestMethod.GET)
	public ResponseDto<InstallationDto> getInstallation() {
		return responseBuilder.build(installationServiceFacade.getInstallation());
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseDto<AuthenticationDto> authenticate(@Valid @RequestBody CredentialsDto aCredentials) throws InvalidCredentialsException {
		return responseBuilder.build(userServiceFacade.authenticate(aCredentials));
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseDto<UserDto> logout(ServletRequest aRequest) throws InvalidTokenException {
		return responseBuilder.build(userServiceFacade.logout(userTokenReader.readAccessToken(aRequest)));
	}

	@RequestMapping(value = "/currentUser", method = RequestMethod.GET)
	public ResponseDto<UserDto> getCurrentUser() throws NotAuthenticatedException {
		return responseBuilder.build(userServiceFacade.getAuthenticatedUser());
	}

	@RequestMapping(value = "/refreshToken", method = RequestMethod.POST)
	public ResponseDto<AuthenticationDto> refreshToken(ServletRequest aRequest) throws InvalidTokenException {
		return responseBuilder.build(userServiceFacade.refreshToken(userTokenReader.readRefreshToken(aRequest)));
	}

	@RequestMapping(value = "/currentUser", method = RequestMethod.PUT)
	public ResponseDto<UserDto> updateCurrentUser(@Valid @RequestBody UpdateCurrentUserCommandDto aCommand) throws NotAuthenticatedException,
			NotAuthorizedException, InvalidPasswordException, UserNotFoundException, UserExistsException, SelfRoleModificationException {
		return responseBuilder.build(userServiceFacade.updateAuthenticatedUser(aCommand));
	}

	@RequestMapping(value = "/artists", method = RequestMethod.GET)
	public ResponseDto<List<ArtistDto>> getArtists() {
		return responseBuilder.build(songServiceFacade.getArtists());
	}

	@RequestMapping(value = "/artistAlbums/{aArtistIdOrName}", method = RequestMethod.GET)
	public ResponseDto<ArtistAlbumsDto> getArtistSongs(@PathVariable String aArtistIdOrName) throws ObjectNotFoundException {
		return responseBuilder.build(songServiceFacade.getArtistSongs(aArtistIdOrName));
	}

	@RequestMapping(value = "/randomSongs", method = RequestMethod.GET)
	public ResponseDto<List<SongDto>> getRandomSongs(@RequestParam(value = "count", defaultValue = "10") int aCount,
													 @RequestParam(value = "artist", required = false) String aArtistIdOrName) throws InvalidArgumentException {
		if (aArtistIdOrName != null) {
			return responseBuilder.build(songServiceFacade.getRandomArtistSongs(aCount, aArtistIdOrName));
		} else {
			return responseBuilder.build(songServiceFacade.getRandomSongs(aCount));
		}
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ResponseDto<SearchDto> search(@RequestParam("text") String aText) {

		SearchQueryDto query = new SearchQueryDto();

		query.setText(aText);

		return responseBuilder.build(songServiceFacade.search(query));
	}

	@RequestMapping(value = "/scanStatus", method = RequestMethod.GET)
	public ResponseDto<ScanStatusDto> getScanStatus() {
		return responseBuilder.build(scanServiceFacade.getScanStatus());
	}

	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public ResponseDto<PagedListDto<UserDto>> getUsers(@RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
													   @RequestParam(value = "pageSize", defaultValue = "25") int aPageSize) throws InvalidArgumentException {
		return responseBuilder.build(userServiceFacade.getAll(aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/admin/users/{aId}", method = RequestMethod.GET)
	public ResponseDto<UserDto> getUser(@PathVariable Long aId) throws ObjectNotFoundException {
		return responseBuilder.build(userServiceFacade.getById(aId));
	}

	@RequestMapping(value = "/admin/users/{aId}", method = RequestMethod.DELETE)
	public ResponseDto<Void> deleteUser(@PathVariable Long aId) throws UserNotFoundException, SelfDeletionException {

		userServiceFacade.delete(aId);

		return responseBuilder.build();
	}

	@RequestMapping(value = "/admin/users", method = RequestMethod.POST)
	public ResponseDto<UserDto> createUser(@Valid @RequestBody CreateUserCommandDto aCommand) throws UserExistsException {
		return responseBuilder.build(userServiceFacade.create(aCommand));
	}

	@RequestMapping(value = "/admin/users", method = RequestMethod.PUT)
	public ResponseDto<UserDto> updateUser(@Valid @RequestBody UpdateUserCommandDto aCommand) throws UserNotFoundException, UserExistsException, SelfRoleModificationException {
		return responseBuilder.build(userServiceFacade.update(aCommand));
	}

	@RequestMapping(value = "/admin/config", method = RequestMethod.GET)
	public ResponseDto<ConfigDto> getConfig() {
		return responseBuilder.build(configServiceFacade.get());
	}

	@RequestMapping(value = "/admin/config", method = RequestMethod.PUT)
	public ResponseDto<ConfigDto> saveConfig(@Valid @RequestBody ConfigDto aConfig) {
		return responseBuilder.build(configServiceFacade.save(aConfig));
	}

	@RequestMapping(value = "/admin/log", method = RequestMethod.GET)
	public ResponseDto<PagedListDto<LogMessageDto>> getLog(@RequestParam(value = "type", required = false) LogMessageDto.Type aType,
														   @RequestParam(value = "minDate", required = false) Long aMinDate,
														   @RequestParam(value = "maxDate", required = false) Long aMaxDate,
														   @RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
														   @RequestParam(value = "pageSize", defaultValue = "25") int aPageSize) throws InvalidArgumentException {

		LogQueryDto query = new LogQueryDto();

		query.setType(aType);
		query.setMinDate(aMinDate != null ? new Date(aMinDate) : null);
		query.setMaxDate(aMaxDate != null ? new Date(aMaxDate) : null);

		return responseBuilder.build(logServiceFacade.getByQuery(query, aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/admin/scanJobs", method = RequestMethod.GET)
	public ResponseDto<PagedListDto<ScanJobDto>> getScanJobs(@RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
															 @RequestParam(value = "pageSize", defaultValue = "25") int aPageSize) throws InvalidArgumentException {
		return responseBuilder.build(scanServiceFacade.getScanJobs(aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/admin/scanJobs/{aId}", method = RequestMethod.GET)
	public ResponseDto<ScanJobDto> getScanJob(@PathVariable Long aId) throws ObjectNotFoundException {
		return responseBuilder.build(scanServiceFacade.getScanJob(aId));
	}

	@RequestMapping(value = "/admin/scanResults", method = RequestMethod.GET)
	public ResponseDto<PagedListDto<ScanResultDto>> getScanResults(@RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
																   @RequestParam(value = "pageSize", defaultValue = "25") int aPageSize) throws InvalidArgumentException {
		return responseBuilder.build(scanServiceFacade.getScanResults(aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/admin/scanResults/{aId}", method = RequestMethod.GET)
	public ResponseDto<ScanResultDto> getScanResult(@PathVariable Long aId) throws ObjectNotFoundException {
		return responseBuilder.build(scanServiceFacade.getScanResult(aId));
	}

	@RequestMapping(value = "/admin/startScanJob", method = RequestMethod.POST)
	public ResponseDto<ScanJobDto> startScanJob() throws LibraryNotDefinedException {
		return responseBuilder.build(scanServiceFacade.startScanJob());
	}

	@RequestMapping(value = "/admin/artworkUpload/{aId}", method = RequestMethod.GET)
	public ResponseDto<ArtworkUploadDto> getArtworkUpload(@PathVariable Long aId) throws ObjectNotFoundException {
		return responseBuilder.build(uploadService.getArtworkUpload(aId));
	}

	@RequestMapping(value = "/admin/artworkUpload", method = RequestMethod.POST)
	public ResponseDto<ArtworkUploadDto> uploadArtwork(@RequestParam("file") MultipartFile aFile) throws ArtworkUploadFormatException {
		return responseBuilder.build(uploadService.uploadArtwork(aFile));
	}

	@RequestMapping(value = "/admin/getSongData", method = RequestMethod.POST)
	public ResponseDto<List<SongDataDto>> getSongData(@RequestBody List<Long> aSongIds) throws ObjectNotFoundException, InvalidArgumentException {
		return responseBuilder.build(songServiceFacade.getSongData(aSongIds));
	}

	@RequestMapping(value = "/admin/startEditJob", method = RequestMethod.POST)
	public ResponseDto<ScanJobDto> startEditJob(@Valid @RequestBody ScanEditCommandDto aCommand) throws ArtworkUploadNotFoundException {
		return responseBuilder.build(scanServiceFacade.startEditJob(aCommand));
	}

}
