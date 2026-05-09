package com.jonet.eventbooking.customexception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jonet.eventbooking.dto.ErrorMessage;

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
}
