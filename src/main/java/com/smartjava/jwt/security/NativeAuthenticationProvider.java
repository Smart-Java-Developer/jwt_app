package com.smartjava.jwt.security;

import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.smartjava.jwt.model.User;
import com.smartjava.jwt.repository.UserRepository;

@Component
public class NativeAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(NativeAuthenticationProvider.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Authentication serverAuthentication(Authentication auth) {

		String username = auth.getName();

		Date currentDate = new Date();
		java.sql.Date date = new java.sql.Date(currentDate.getTime());
		try {
			User user = userRepository.findByUsernameIgnoreCase(username);

			if (user == null) {
				throw new UsernameNotFoundException("User not found with username or email : " + username);
			}
//
//String resetPassword = user.getResetPassword() != null ? user.getResetPassword() : "N";
//Date startDate = user.getStartDate() != null ? new java.sql.Date(user.getStartDate().getTime()) : date;
//Date endDate = user.getEndDate() != null ? new java.sql.Date(user.getEndDate().getTime()) : date;
//String enabled = user.getEnabled() != null ? user.getEnabled() : "Y";
//
//if (enabled.equals("N")) {
//System.out.println("Is User Account Active : " + enabled);
//throw new AccountExpiredException("Account is not active, please contact administrator");
//}
//
//if (startDate.after(currentDate)) {
//System.out.println("User Start Date : " + startDate);
//throw new AccountExpiredException("Unable to log in. You can access the account from " + startDate);
//}
//
//if (endDate.before(currentDate)) {
//System.out.println("User End Date : " + endDate);
//throw new AccountExpiredException("Unable to log in. Your account access was expired on " + endDate);
//}
//
//if (resetPassword.equals("Y") || resetPassword.equals("Yes")) {
//System.out.println("Please reset password");
//throw new AppException("System generated password need to changed before login");
//}
//
			if (passwordEncoder.matches(auth.getCredentials().toString(), user.getPassword())) {
				return new UsernamePasswordAuthenticationToken(username, "", Collections.emptyList());

			} else {
				throw new BadCredentialsException("Invalid Username/Password");
			}
		} catch (Exception e) {
			LOGGER.error("Native User authentication failed");
			throw new BadCredentialsException("Invalid Username/Password");
		}
	}

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		return serverAuthentication(auth);
	}

	@Override
	public boolean supports(Class<?> auth) {
		return auth.equals(UsernamePasswordAuthenticationToken.class);
	}
}