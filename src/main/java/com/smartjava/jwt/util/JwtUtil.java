package com.smartjava.jwt.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.smartjava.jwt.exception.TokenFromDifferentDeviceException;
import com.smartjava.jwt.exception.UnauthorizedTokenException;
import com.smartjava.jwt.repository.UserRepository;
import com.smartjava.jwt.repository.UserSessionRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {

	@Autowired
	private UserSessionRepository userSessionRepository;
	
	@Autowired
	private UserRepository userRepository;

	private String SECRET_KEY = "@*9939u*%#";

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractUserId(String token) {
		return extractClaim(token, Claims::getId);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public String extractInfoFromToken(String token, String typeKey) {
		return extractClaim(token, claims -> claims.get(typeKey, String.class));
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		try {
			return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			throw new UnauthorizedTokenException("Unauthorized or Invalid Token");
		}
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails, HttpServletRequest request, String sessionId) {
		Map<String, Object> claims = new HashMap<>();
		String ipAddress = getClientIp(request);
		String browserInfo = getBrowserInfo(request);
		claims.put("ip", ipAddress);
		claims.put("browser", browserInfo);
		claims.put("sessionId", sessionId);
		claims.put("userId", userRepository.findByUsernameIgnoreCase(userDetails.getUsername()).getId() + "");
		return createToken(claims, userDetails.getUsername());
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuer("Smart Java Developer")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10-hour expiration
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails, HttpServletRequest request,
			boolean checkSession) throws UnauthorizedTokenException, TokenFromDifferentDeviceException {
		final String username = extractUsername(token);
		final String tokenSessionId = extractInfoFromToken(token, "sessionId");
		final String tokenIp = extractInfoFromToken(token, "ip");
		final String tokenBrowser = extractInfoFromToken(token, "browser");
		final Long userId = Long.parseLong(extractInfoFromToken(token, "userId"));

		String requestIp = getClientIp(request);
		String requestBrowser = getBrowserInfo(request);

		boolean sessionIsValid = true;
		if (checkSession) {
			sessionIsValid = checkSessionInDatabase(userId, tokenSessionId);
		}

		if (!username.equals(userDetails.getUsername()) || isTokenExpired(token)) {
			throw new UnauthorizedTokenException("Unauthorized: Token is invalid or expired");
		}

		if (!tokenIp.equals(requestIp) || !tokenBrowser.equals(requestBrowser)) {
			throw new TokenFromDifferentDeviceException("Unauthorized: Token is not from the current device");
		}

		if (!sessionIsValid) {
			System.err.println(
					"#################  \"Unauthorized: Session is invalid or has expired\" ######################");
			throw new UnauthorizedTokenException("Unauthorized: Session is invalid or has expired");
		}

		return true;
	}

	public String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public String getBrowserInfo(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

	public boolean checkSessionInDatabase(Long userId, String sessionId) {
		return userSessionRepository.existsByUserIdAndSessionIdAndActive(userId, sessionId, true);
	}
}