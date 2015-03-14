package net.dorokhov.pony.web.shared;

public class ErrorCodes {

	public static final String CLIENT_REQUEST_FAILED = "errorClientRequestFailed";
	public static final String CLIENT_OFFLINE = "errorClientOffline";

	public static final String INVALID_CONTENT_TYPE = "errorInvalidContentType";
	public static final String INVALID_REQUEST = "errorInvalidRequest";
	public static final String ACCESS_DENIED = "errorAccessDenied";
	public static final String VALIDATION = "errorValidation";
	public static final String UNEXPECTED = "errorUnexpected";

	public static final String INVALID_CREDENTIALS = "errorInvalidCredentials";
	public static final String INVALID_PASSWORD = "errorInvalidPassword";

	public static final String SCAN_JOB_NOT_FOUND = "errorScanJobNotFound";
	public static final String SCAN_RESULT_NOT_FOUND = "errorScanResultNotFound";
	public static final String ARTIST_NOT_FOUND = "errorArtistNotFound";
	public static final String SONG_NOT_FOUND = "errorSongNotFound";
	public static final String USER_NOT_FOUND = "errorUserNotFound";
	public static final String ARTWORK_UPLOAD_NOT_FOUND = "errorArtworkUploadNotFound";

	public static final String ARTWORK_UPLOAD_FORMAT = "errorArtworkUploadFormat";

	public static final String LIBRARY_NOT_DEFINED = "errorLibraryNotDefined";

	public static final String MAX_UPLOAD_SIZE_EXCEEDED = "errorMaxUploadSizeExceeded";

	public static final String USER_SELF_DELETION = "errorUserSelfDeletion";

	public static final String PAGE_NUMBER_INVALID = "errorPageNumberInvalid";
	public static final String PAGE_SIZE_INVALID = "errorPageSizeInvalid";

	public static final String SONGS_COUNT_INVALID = "errorSongsCountInvalid";

	private ErrorCodes() {}

}
