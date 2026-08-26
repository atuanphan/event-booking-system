package com.jonet.eventbooking.customexception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jonet.eventbooking.dto.ErrorMessage;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
			HttpStatusCode status, WebRequest request) {
		Map<String, Object> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
		ErrorMessage message = new ErrorMessage();
		message.setTimestamp(LocalDateTime.now());
		message.setStatus(status.value());
		message.setErrors(errors);
		message.setPath(request.getDescription(false).replace("uri=", ""));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}
	
	@ExceptionHandler(exception = ResourceAlreadyExistsException.class)
	public ResponseEntity<Object> handleResourceAlreadyExists(ResourceAlreadyExistsException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(
				ErrorMessage.builder()
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.CONFLICT.value())
				.path(request.getRequestURI())
				.build());
	}
	
	@ExceptionHandler(exception = EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				ErrorMessage.builder()
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.NOT_FOUND.value())
				.path(request.getRequestURI())
				.build());
	}
	
	@ExceptionHandler(exception = DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(
				ErrorMessage.builder()
				.message("Duplicate data")
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.CONFLICT.value())
				.path(request.getRequestURI())
				.build());
	}
	
	@ExceptionHandler(exception = BadRequestException.class)
	public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(
				ErrorMessage.builder()
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.CONFLICT.value())
				.path(request.getRequestURI())
				.build());
	}

	@ExceptionHandler(exception = InvalidRefreshTokenException.class)
	public ResponseEntity<Object> handleInvalidRefreshTokenException(BadRequestException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
				ErrorMessage.builder()
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.UNAUTHORIZED.value())
				.path(request.getRequestURI())
				.build());
	}
}
