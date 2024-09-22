package com.smartjava.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {

	private String tokenType;
	private final String jwt;
}
