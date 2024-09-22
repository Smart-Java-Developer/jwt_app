package com.smartjava.jwt.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartjava.jwt.exception.TokenFromDifferentDeviceException;
import com.smartjava.jwt.exception.UnauthorizedTokenException;
import com.smartjava.jwt.security.UserDetailService;
import com.smartjava.jwt.util.JwtUtil;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDetailService userDetailService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String authHeader = request.getHeader("Authorization");
			String userName = null;
			String jwt = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				jwt = authHeader.substring(7);
				userName = jwtUtil.extractUsername(jwt);
			}

			if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailService.loadUserByUsername(userName);

				if (jwtUtil.validateToken(jwt, userDetails, request, true)) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}

			filterChain.doFilter(request, response);

		} catch (UnauthorizedTokenException | TokenFromDifferentDeviceException ex) {
			request.setAttribute("exceptionMessage", ex.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());

		} catch (Exception ex) {
			request.setAttribute("exceptionMessage", ex.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
		}
	}
}