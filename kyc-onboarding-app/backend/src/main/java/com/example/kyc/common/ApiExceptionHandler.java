package com.example.kyc.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
    var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(f -> Map.of(
            "field", f.getField(),
            "message", f.getDefaultMessage()
        )).toList();
    return ResponseEntity.badRequest().body(Map.of(
        "error", "Validation failed",
        "details", fieldErrors
    ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> other(Exception ex) {
    // log stacktrace for troubleshooting, return safe message to client
    ex.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Server error"));
  }
}
