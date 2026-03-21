package com.example.exception;

import com.example.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
    MethodArgumentNotValidException ex,
    HttpServletRequest request) {

    Map<String, Object> details = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      details.put(error.getField(), error.getDefaultMessage());
    }

    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(Instant.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Validation Failed")
      .message("Invalid request content")
      .path(request.getRequestURI())
      .details(details)
      .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
    ConstraintViolationException ex,
    HttpServletRequest request) {

    Map<String, Object> details = new HashMap<>();
    ex.getConstraintViolations().forEach(violation -> {
      String field = violation.getPropertyPath().toString();
      details.put(field, violation.getMessage());
    });

    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(Instant.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Constraint Violation")
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .details(details)
      .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTaskNotFound(
    TaskNotFoundException ex,
    HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(Instant.now())
      .status(HttpStatus.NOT_FOUND.value())
      .error("Task Not Found")
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .details(new HashMap<>())
      .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParams(
    MissingServletRequestParameterException ex,
    HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(Instant.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Missing Parameter")
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .details(new HashMap<>())
      .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(
    HttpMessageNotReadableException ex,
    HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(Instant.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Malformed JSON")
      .message("Invalid request body format")
      .path(request.getRequestURI())
      .details(new HashMap<>())
      .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
    NoHandlerFoundException ex,
    HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(Instant.now())
      .status(HttpStatus.NOT_FOUND.value())
      .error("Endpoint Not Found")
      .message("The requested endpoint does not exist")
      .path(request.getRequestURI())
      .details(new HashMap<>())
      .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
    Exception ex,
    HttpServletRequest request) {

    ex.printStackTrace();

    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(Instant.now())
      .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
      .error("Internal Server Error")
      .message("An unexpected error occurred")
      .path(request.getRequestURI())
      .details(new HashMap<>())
      .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}