package com.smartjava.jwt.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.smartjava.jwt.dto.UserDTO;
import com.smartjava.jwt.model.User;
import com.smartjava.jwt.payload.ApiResponse;
import com.smartjava.jwt.repository.UserRepository;
import com.smartjava.jwt.util.JwtUtil;

@Service
public class AdminService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	public ApiResponse getAllUsers() {
		try {
			List<User> users = userRepository.findAll();
			List<UserDTO> userDTOs = users.stream().map(user -> modelMapper.map(user, UserDTO.class))
					.collect(Collectors.toList());
			return new ApiResponse(true, "Success", HttpStatus.OK, userDTOs, Collections.emptyList());
		} catch (Exception e) {
			return new ApiResponse(false, "Unable to fetch Users", HttpStatus.INTERNAL_SERVER_ERROR, null,
					Collections.emptyList());
		}
	}

	
}
