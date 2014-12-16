package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.core.user.exception.InvalidCredentialsException;
import net.dorokhov.pony.core.user.exception.InvalidPasswordException;
import net.dorokhov.pony.core.user.exception.UserNotFoundException;
import net.dorokhov.pony.web.domain.ErrorDto;
import net.dorokhov.pony.web.domain.ResponseDto;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.service.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
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

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseDto onAccessDeniedError(AccessDeniedException aException, HttpServletRequest aRequest) {

		log.warn("Access denied to [" + aRequest.getRequestURL().toString() + "].");

		return responseBuilder.build(new ErrorDto("errorAccessDenied", aException.getMessage()));
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

	@ExceptionHandler(LibraryNotDefinedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseDto onLibraryNotDefinedError(LibraryNotDefinedException aException) {

		log.warn("Library is not defined.");

		return responseBuilder.build(new ErrorDto("errorLibraryNotDefined", aException.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseDto onValidationError(MethodArgumentNotValidException aException) {

		log.debug(aException.getMessage());

		List<ErrorDto> errorList = new ArrayList<>();

		for (FieldError fieldError : aException.getBindingResult().getFieldErrors()) {
			errorList.add(new ErrorDto("errorValidation." + fieldError.getCode(), fieldError.getDefaultMessage(), fieldError.getField()));
		}

		return responseBuilder.build(errorList);
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

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseDto onUnexpectedError(Exception aException) {

		log.error("Unexpected error occurred.", aException);

		return responseBuilder.build(new ErrorDto("errorUnexpected", "Unexpected error occurred."));
	}
}
