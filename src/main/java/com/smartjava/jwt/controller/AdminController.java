package com.smartjava.jwt.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartjava.jwt.payload.ApiResponse;
import com.smartjava.jwt.service.AdminService;
import com.smartjava.jwt.service.SessionService;

@RestController
@RequestMapping("/app")
public class AdminController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private SessionService sessionService;

	@GetMapping("/users")
	public ApiResponse getAllUsers() {
		return adminService.getAllUsers();
	}

	@GetMapping("/user/terminateSessions")
	public ApiResponse terminateOtherSessions(HttpServletRequest httpServletRequest) {
		String bearerToken = httpServletRequest.getHeader("Authorization");
		System.out.println("bearerToken :: " + bearerToken);
		return sessionService.terminateOtherSessions(bearerToken);
	}

}
