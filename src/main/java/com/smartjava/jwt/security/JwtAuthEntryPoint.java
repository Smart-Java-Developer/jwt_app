package com.smartjava.jwt.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		// Retrieve the custom exception message from request attributes, if set
		String exceptionMessage = (String) request.getAttribute("exceptionMessage");

		if (!(authException instanceof Exception) &&  exceptionMessage != null) {
			System.err.println("Custom exceptionMessage In JwtAuthEntryPoint :: " + exceptionMessage);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exceptionMessage);
		} else {
			System.err.println("Default exceptionMessage In JwtAuthEntryPoint :: " + authException.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
		}
	}
}
