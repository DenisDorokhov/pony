package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.core.user.exception.InvalidCredentialsException;
import net.dorokhov.pony.core.user.exception.InvalidPasswordException;
import net.dorokhov.pony.core.user.exception.UserNotFoundException;
import net.dorokhov.pony.core.user.exception.UserSelfDeletionException;
import net.dorokhov.pony.web.domain.ErrorDto;
import net.dorokhov.pony.web.domain.ResponseDto;
import net.dorokhov.pony.web.exception.ArtworkUploadFormatException;
import net.dorokhov.pony.web.exception.ArtworkUploadNotFoundException;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.service.ResponseBuilder;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
	public ResponseDto onInvalidCredentialsException(InvalidCredentialsException aException) {

		log.warn("Credentials are invalid.");

		return responseBuilder.build(new ErrorDto("errorInvalidCredentials", aException.getMessage()));
	}

	@ExceptionHandler(InvalidPasswordException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseDto onInvalidPasswordException(InvalidPasswordException aException) {

		log.warn("Password is invalid.");

		return responseBuilder.build(new ErrorDto("errorInvalidPassword", aException.getMessage()));
	}

	@ExceptionHandler(ObjectNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseDto onObjectNotFoundError(ObjectNotFoundException aException) {

		log.debug(aException.getMessage());

		return responseBuilder.build(new ErrorDto(aException.getErrorCode(), aException.getMessage(), Arrays.asList(aException.getObjectId().toString())));
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onUserNotFoundError(UserNotFoundException aException) {

		log.warn(aException.getMessage());

		String userId = null;
		if (aException.getUserId() != null) {
			userId = aException.getUserId().toString();
		}

		return responseBuilder.build(new ErrorDto("errorUserNotFound", aException.getMessage(), Arrays.asList(userId)));
	}

	@ExceptionHandler(UserSelfDeletionException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onUserSelfDeletionError(UserSelfDeletionException aException) {

		log.debug(aException.getMessage());

		String userId = null;
		if (aException.getUserId() != null) {
			userId = aException.getUserId().toString();
		}

		return responseBuilder.build(new ErrorDto("errorUserSelfDeletion", aException.getMessage(), Arrays.asList(userId)));
	}

	@ExceptionHandler(LibraryNotDefinedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseDto onLibraryNotDefinedError(LibraryNotDefinedException aException) {

		log.warn("Library is not defined.");

		return responseBuilder.build(new ErrorDto("errorLibraryNotDefined", aException.getMessage()));
	}

	@ExceptionHandler(ArtworkUploadFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onArtworkUploadFormatError(ArtworkUploadFormatException aException) {
		return responseBuilder.build(new ErrorDto("errorArtworkUploadFormat", aException.getMessage()));
	}

	@ExceptionHandler(ArtworkUploadNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onArtworkUploadNotFoundError(ArtworkUploadNotFoundException aException) {

		String artworkUploadId = null;
		if (aException.getArtworkUploadId() != null) {
			artworkUploadId = aException.getArtworkUploadId().toString();
		}

		return responseBuilder.build(new ErrorDto("errorArtworkUploadNotFound", aException.getMessage(), Arrays.asList(artworkUploadId)));
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

			errorList.add(new ErrorDto("errorValidation." + fieldError.getCode(), fieldError.getDefaultMessage(), fieldError.getField(), errorArguments));
		}

		return responseBuilder.build(errorList);
	}

	@ExceptionHandler(NotImplementedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onNotImplementedError(NotImplementedException aException) {

		log.warn(aException.getMessage(), aException);

		return responseBuilder.build(new ErrorDto("errorNotImplemented", aException.getMessage()));
	}

	@ExceptionHandler(HttpMediaTypeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onContentTypeNotSupported(HttpMediaTypeException aException) {

		log.debug(aException.getMessage());

		return responseBuilder.build(new ErrorDto("errorInvalidContentType", "Invalid content type."));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onMessageNotReadableError(HttpMessageNotReadableException aException) {

		log.debug(aException.getMessage());

		return responseBuilder.build(new ErrorDto("errorInvalidRequest", "Invalid request."));
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseDto onUnexpectedError(Throwable aException) {

		log.error("Unexpected error occurred.", aException);

		return responseBuilder.build(new ErrorDto("errorUnexpected", "Unexpected error occurred."));
	}

}
