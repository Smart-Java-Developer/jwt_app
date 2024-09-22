package com.smartjava.jwt.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartjava.jwt.model.AuthenticationRequest;
import com.smartjava.jwt.model.AuthenticationResponse;
import com.smartjava.jwt.model.User;
import com.smartjava.jwt.repository.UserRepository;
import com.smartjava.jwt.security.UserDetailService;
import com.smartjava.jwt.service.SessionService;
import com.smartjava.jwt.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private UserDetailService userDetailService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SessionService sessionService;

	/**
	 * Authenticate user and generate JWT token. If the user is logging in from a
	 * new device, previous sessions will be terminated.
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(
			@RequestParam(required = false, defaultValue = "N") String terminateOtherSession,
			@RequestBody AuthenticationRequest request, HttpServletRequest httpServletRequest) throws Exception {
		try {
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (Exception e) {
			throw new Exception("Invalid Credentials", e);
		}
//		$2a$12$.dZf.LtBLQIae16Z9Lt1ueqtAQeMI5Yrh4YnCmvpSHXxkWQHZbqAS
		UserDetails userDetails = userDetailService.loadUserByUsername(request.getUsername());

		User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername());

		String newSessionId = java.util.UUID.randomUUID().toString();

		if ("Y".equalsIgnoreCase(terminateOtherSession)) {
			sessionService.removeAllSessionsExceptCurrent(user.getId(), newSessionId);
		}

		String token = jwtUtil.generateToken(userDetails, httpServletRequest, newSessionId);

		sessionService.createNewSession(user.getId(), newSessionId, jwtUtil.getClientIp(httpServletRequest),
				jwtUtil.getBrowserInfo(httpServletRequest), token);

		AuthenticationResponse response = new AuthenticationResponse("Bearer", token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Logout the user by invalidating the token and removing the session.
	 */
	@PostMapping("/signout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
		try {
			System.err.println("Calling logout");
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);

				// Extract session ID from the token
				String sessionId = jwtUtil.extractInfoFromToken(token, "sessionId");
				String username = jwtUtil.extractUsername(token);

				User user = userRepository.findByUsernameIgnoreCase(username);

				// Invalidate the session in the session store
				sessionService.terminateSession(user.getId(), sessionId);

				SecurityContextHolder.getContext().setAuthentication(null);

				return new ResponseEntity<>("User: '" + user.getUsername() + "' Logout successful", HttpStatus.OK);

			} else {
				return new ResponseEntity<>("Logout failed", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Logout failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
