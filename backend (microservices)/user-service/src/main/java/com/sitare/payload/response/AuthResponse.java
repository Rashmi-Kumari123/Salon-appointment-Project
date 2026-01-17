package com.sitare.payload.response;

import com.sitare.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	private String jwt;
	private String refresh_token;
	private String message;
	private String title;
	private UserRole role;
	
}
