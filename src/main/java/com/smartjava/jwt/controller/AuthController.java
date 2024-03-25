package com.smartjava.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.smartjava.jwt.model.AuthenticationRequest;
import com.smartjava.jwt.model.AuthenticationResponse;
import com.smartjava.jwt.security.UserDetailService;
import com.smartjava.jwt.util.JwtUtil;

@RestController
public class AuthController {

	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private UserDetailService userDetailService;
	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/authenticate")
	public ResponseEntity<?> authentiCate(@RequestBody AuthenticationRequest request) throws Exception {
		try {
			authManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} catch (Exception e) {
			throw new Exception("Invalid Credentials");
		}
		UserDetails userDetails = userDetailService.loadUserByUsername(request.getUsername());

		String token = jwtUtil.generateToken(userDetails);

		AuthenticationResponse response = new AuthenticationResponse(token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
