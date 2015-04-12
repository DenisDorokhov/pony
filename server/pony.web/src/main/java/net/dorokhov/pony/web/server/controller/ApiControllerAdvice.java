package net.dorokhov.pony.web.server.controller;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.core.user.exception.*;
import net.dorokhov.pony.web.shared.ErrorCodes;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.ResponseDto;
import net.dorokhov.pony.web.server.exception.ArtworkUploadFormatException;
import net.dorokhov.pony.web.server.exception.ArtworkUploadNotFoundException;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.server.service.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice(assignableTypes = ApiController.class)
@ResponseBody
public class ApiControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ResponseBuilder responseBuilder;

	@Autowired
	public void setResponseBuilder(ResponseBuilder aResponseBuilder) {
		responseBuilder = aResponseBuilder;
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseDto onInvalidCredentials(InvalidCredentialsException aException) {

		log.warn("Credentials are invalid.");

		return responseBuilder.build(new ErrorDto(ErrorCodes.INVALID_CREDENTIALS, aException.getMessage()));
	}

	@ExceptionHandler(InvalidPasswordException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseDto onInvalidPassword(InvalidPasswordException aException) {

		log.warn("Password is invalid.");

		return responseBuilder.build(new ErrorDto(ErrorCodes.INVALID_PASSWORD, aException.getMessage()));
	}

	@ExceptionHandler(InvalidTokenException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseDto onInvalidToken() {

		log.warn("Token is invalid.");

		return responseBuilder.build(new ErrorDto(ErrorCodes.ACCESS_DENIED, "Access denied."));
	}

	@ExceptionHandler(ObjectNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseDto onObjectNotFound(ObjectNotFoundException aException) {

		log.debug(aException.getMessage());

		return responseBuilder.build(new ErrorDto(aException.getErrorCode(), aException.getMessage(), aException.getObjectId() != null ? Arrays.asList(aException.getObjectId().toString()) : null));
	}

	@ExceptionHandler(InvalidArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onInvalidRequest(InvalidArgumentException aException) {

		log.debug(aException.getMessage());

		return responseBuilder.build(new ErrorDto(aException.getErrorCode(), aException.getMessage(), aException.getArguments()));
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onUserNotFound(UserNotFoundException aException) {

		log.warn(aException.getMessage());

		String userId = null;
		if (aException.getUserId() != null) {
			userId = aException.getUserId().toString();
		}

		return responseBuilder.build(new ErrorDto(ErrorCodes.USER_NOT_FOUND, aException.getMessage(), Arrays.asList(userId)));
	}

	@ExceptionHandler(SelfDeletionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onUserSelfDeletion(SelfDeletionException aException) {

		log.debug(aException.getMessage());

		String userId = null;
		if (aException.getUserId() != null) {
			userId = aException.getUserId().toString();
		}

		return responseBuilder.build(new ErrorDto(ErrorCodes.USER_SELF_DELETION, aException.getMessage(), Arrays.asList(userId)));
	}

	@ExceptionHandler(SelfRoleModificationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onUserSelfRoleModification(SelfRoleModificationException aException) {

		log.debug(aException.getMessage());

		String userId = null;
		if (aException.getUserId() != null) {
			userId = aException.getUserId().toString();
		}

		return responseBuilder.build(new ErrorDto(ErrorCodes.USER_SELF_ROLE_MODIFICATION, aException.getMessage(), Arrays.asList(userId)));
	}

	@ExceptionHandler(LibraryNotDefinedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseDto onLibraryNotDefined(LibraryNotDefinedException aException) {

		log.warn("Library is not defined.");

		return responseBuilder.build(new ErrorDto(ErrorCodes.LIBRARY_NOT_DEFINED, aException.getMessage()));
	}

	@ExceptionHandler(ArtworkUploadFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onArtworkUploadFormatError(ArtworkUploadFormatException aException) {
		return responseBuilder.build(new ErrorDto(ErrorCodes.ARTWORK_UPLOAD_FORMAT, aException.getMessage()));
	}

	@ExceptionHandler(ArtworkUploadNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onArtworkUploadNotFound(ArtworkUploadNotFoundException aException) {

		String artworkUploadId = null;
		if (aException.getObjectId() != null) {
			artworkUploadId = aException.getObjectId().toString();
		}

		return responseBuilder.build(new ErrorDto(ErrorCodes.ARTWORK_UPLOAD_NOT_FOUND, aException.getMessage(), Arrays.asList(artworkUploadId)));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onValidationError(MethodArgumentNotValidException aException) {

		List<ErrorDto> errorList = new ArrayList<>();

		for (FieldError fieldError : aException.getBindingResult().getFieldErrors()) {

			List<String> errorArguments = new ArrayList<>();
			for (Object argument : fieldError.getArguments()) {
				if (argument instanceof Byte
						|| argument instanceof Short
						|| argument instanceof Integer
						|| argument instanceof Long
						|| argument instanceof Float
						|| argument instanceof Double
						|| argument instanceof Character
						|| argument instanceof Boolean
						|| argument instanceof String) {

					errorArguments.add(argument.toString());
				}
			}

			errorList.add(new ErrorDto(ErrorCodes.VALIDATION + "." + fieldError.getCode(), fieldError.getDefaultMessage(), fieldError.getField(), errorArguments));
		}

		return responseBuilder.build(errorList);
	}

	@ExceptionHandler(HttpMediaTypeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onContentTypeNotSupported(HttpMediaTypeException aException) {

		log.debug(aException.getMessage());

		return responseBuilder.build(new ErrorDto(ErrorCodes.INVALID_CONTENT_TYPE, "Invalid content type."));
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onMessageNotReadable(Exception aException) {

		log.debug(aException.getMessage());

		return responseBuilder.build(new ErrorDto(ErrorCodes.INVALID_REQUEST, "Invalid request."));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onMaxUploadSizeExceeded(MaxUploadSizeExceededException aException) {

		log.warn(aException.getMessage());

		return responseBuilder.build(new ErrorDto(ErrorCodes.MAX_UPLOAD_SIZE_EXCEEDED, aException.getMessage(), Arrays.asList(String.valueOf(aException.getMaxUploadSize()))));
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Object onUnexpectedError(Throwable aException) {

		if (aException.getCause() instanceof IOException) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); // avoid logging of broken pipe exceptions
		}

		log.error("Unexpected error occurred.", aException);

		return responseBuilder.build(new ErrorDto(ErrorCodes.UNEXPECTED, "Unexpected error occurred."));
	}

}
