package com.example.demo.exception;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.dto.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {

		return ResponseEntity.badRequest().body(new ApiError("VALIDATION_ERROR", "Invalid request", Instant.now()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> badRequest(IllegalArgumentException ex) {

		return ResponseEntity.badRequest().body(new ApiError("BAD_REQUEST", ex.getMessage(), Instant.now()));
	}
}