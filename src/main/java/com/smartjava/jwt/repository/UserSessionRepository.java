package com.smartjava.jwt.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.smartjava.jwt.model.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

	// Find active session by userId and sessionId
	UserSession findByUserIdAndSessionIdAndActiveTrue(Long userId, String sessionId);

	// Find all active sessions for a specific user
	List<UserSession> findByUserIdAndActiveTrue(Long userId);

	Boolean existsByUserIdAndSessionIdAndActive(Long userId, String sessionId, boolean b);

//	@Modifying
//	@Transactional
//	@Query(value = "UPDATE USER_SESSIONS SET ACTIVE = 0 WHERE USER_ID = ?1 AND SESSION_ID <> ?2")
//	void terminateAllOtherSessions(Long userId, String currentSessionId);
}
