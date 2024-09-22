package com.smartjava.jwt.payload;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {

	private boolean success;
	private String message;
	private HttpStatus status;
	private Object data;
	private List<String> errors;
}
