package com.smartjava.jwt.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smartjava.jwt.payload.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
		request.setAttribute("exceptionMessage", "Something went wrong: " + ex.getMessage());
		ApiResponse response = new ApiResponse(false, "Something went wrong: " + ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR, null, null);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
