package net.dorokhov.pony.web.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(assignableTypes = FileController.class)
public class FileControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> onUnexpectedError(Exception aException) {

		log.error("Unexpected error occurred.", aException);

		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> onAccessDeniedError(HttpServletRequest aRequest) {

		log.warn("Access denied to [" + aRequest.getRequestURL().toString() + "].");

		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}

}
