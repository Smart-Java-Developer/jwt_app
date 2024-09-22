package com.smartjava.jwt.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smartjava.jwt.repository.UserRepository;

@Service
public class UserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		com.smartjava.jwt.model.User dbUser = userRepository.findByUsernameIgnoreCase(username);

		if (dbUser == null) {
			throw new UsernameNotFoundException("User not exists");
		}

		return new User(username, dbUser.getPassword(), new ArrayList<>());
	}

}
