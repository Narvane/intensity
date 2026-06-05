package br.com.narvane.config.infra.handlers;

import br.com.narvane.config.infra.exceptions.ApiError;
import br.com.narvane.config.infra.exceptions.ApiSubError;
import br.com.narvane.config.infra.exceptions.ApiValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage("Error on request");
        error.setTimestamp(LocalDateTime.now());
        error.setSubErrors(subErrors(ex));
        error.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFound(NoResourceFoundException ex) {
        ApiError error = new ApiError(org.springframework.http.HttpStatus.NOT_FOUND);
        error.setMessage("Resource not found");
        error.setTimestamp(LocalDateTime.now());
        error.setDebugMessage(ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        error.setTimestamp(LocalDateTime.now());
        return buildResponseEntity(error);
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private List<ApiSubError> subErrors(MethodArgumentNotValidException ex) {
        List<ApiSubError> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            ApiValidationError api = new ApiValidationError(
                    ex.getObjectName(),
                    fieldError.getField(),
                    fieldError.getRejectedValue(),
                    fieldError.getDefaultMessage()
            );
            errors.add(api);
        }
        return errors;
    }
}
