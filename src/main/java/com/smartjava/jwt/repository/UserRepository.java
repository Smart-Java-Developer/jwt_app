package com.smartjava.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartjava.jwt.model.User;

public interface UserRepository  extends JpaRepository<User, Long> {

	User findByUsernameIgnoreCase(String username);

}
