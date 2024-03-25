package com.smartjava.jwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class AdminController {

	@GetMapping("/info")
	public String getAppInfo() {
		return "JWT APP";
	}

}
