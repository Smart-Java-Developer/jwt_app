package com.smartjava.jwt.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "USER_SESSIONS")
public class UserSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;
	private String sessionId;
	@Lob
	private String token;
	private String ipAddress;
	private String browserInfo;
	private boolean active;
	private Date terminatedOn;
	private Date loginTime;
}
