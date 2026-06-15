package com.intensity.config;

import com.intensity.common.dto.ErrorResponse;
import com.intensity.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(ApiException.class)
	ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
		return ResponseEntity
				.status(exception.getStatus())
				.body(new ErrorResponse(exception.getCode(), exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(this::formatFieldError)
				.collect(Collectors.joining("; "));

		return ResponseEntity
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new ErrorResponse("VALIDATION_ERROR", message));
	}

	private String formatFieldError(FieldError error) {
		return error.getField() + ": " + error.getDefaultMessage();
	}
}
