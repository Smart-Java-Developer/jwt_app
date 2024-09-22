package com.smartjava.jwt.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.smartjava.jwt.model.UserSession;
import com.smartjava.jwt.payload.ApiResponse;
import com.smartjava.jwt.repository.UserSessionRepository;
import com.smartjava.jwt.util.JwtUtil;

@Service
public class SessionService {

	@Autowired
	private UserSessionRepository userSessionRepository;
	@Autowired
	private JwtUtil jwtUtil;

	// Check if a session is active for the user
	public boolean isSessionActive(Long userId, String sessionId) {
		UserSession session = userSessionRepository.findByUserIdAndSessionIdAndActiveTrue(userId, sessionId);
		return session != null;
	}

	// Remove all sessions for the user except for the current one
	public void removeAllSessionsExceptCurrent(Long userId, String currentSessionId) {
		List<UserSession> sessions = userSessionRepository.findByUserIdAndActiveTrue(userId);
		for (UserSession session : sessions) {
			if (!session.getSessionId().equals(currentSessionId)) {
				session.setActive(false);
				session.setTerminatedOn(new Date());
				userSessionRepository.save(session);
			}
		}
	}

	// Create a new session entry in the database
	public void createNewSession(Long userId, String sessionId, String ipAddress, String browserInfo, String token) {
		UserSession newSession = new UserSession();
		newSession.setUserId(userId);
		newSession.setSessionId(sessionId);
		newSession.setIpAddress(ipAddress);
		newSession.setBrowserInfo(browserInfo);
		newSession.setToken(token);
		newSession.setLoginTime(new Date());
		newSession.setActive(true);
		userSessionRepository.save(newSession);
	}

	public void terminateSession(Long userId, String sessionId) {
		UserSession session = userSessionRepository.findByUserIdAndSessionIdAndActiveTrue(userId, sessionId);
		if (session != null) {
			session.setActive(false);
			userSessionRepository.save(session);
		}
	}

	public void saveUserSession(UserSession userSession) {
		userSessionRepository.save(userSession);

	}

	public ApiResponse terminateOtherSessions(String bearerToken) {

		try {
			if (bearerToken != null && bearerToken.startsWith("Bearer "))
				bearerToken = bearerToken.substring(7);
			else
				return new ApiResponse(false, "UnAuthorized", HttpStatus.INTERNAL_SERVER_ERROR, null,
						Collections.emptyList());

			String sessionId = jwtUtil.extractInfoFromToken(bearerToken, "sessionId");
			String userId = jwtUtil.extractInfoFromToken(bearerToken, "userId");
			System.err.println("session Id :: " + sessionId);
			System.err.println("User Id :: " + userId);

			removeAllSessionsExceptCurrent(Long.parseLong(userId), sessionId);
			return new ApiResponse(true, "Terminated all other sessions", HttpStatus.OK, null, Collections.emptyList());

		} catch (Exception e) {
			e.printStackTrace();
			return new ApiResponse(false, "Unable to terminate other Sessions, Try after sometime",
					HttpStatus.INTERNAL_SERVER_ERROR, null, Collections.emptyList());
		}

	}
}
