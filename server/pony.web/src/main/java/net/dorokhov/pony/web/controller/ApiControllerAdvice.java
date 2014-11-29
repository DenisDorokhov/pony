package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.web.domain.ErrorDto;
import net.dorokhov.pony.web.domain.ResponseDto;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.service.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;

@ControllerAdvice(assignableTypes = ApiController.class)
@ResponseBody
public class ApiControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ResponseBuilder responseBuilder;

	@Autowired
	public void setResponseBuilder(ResponseBuilder aResponseBuilder) {
		responseBuilder = aResponseBuilder;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseDto handleUnexpectedError(Exception aException) {

		log.error("Unexpected error occurred.", aException);

		return responseBuilder.build(new ErrorDto("errorUnexpected", "Unexpected error occurred."));
	}

	@ExceptionHandler(ObjectNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseDto handleNotFoundError(ObjectNotFoundException aException) {

		log.debug("Object could not found.", aException);

		return responseBuilder.build(new ErrorDto(aException.getErrorCode(), aException.getMessage(), Arrays.asList(aException.getId().toString())));
	}
}
