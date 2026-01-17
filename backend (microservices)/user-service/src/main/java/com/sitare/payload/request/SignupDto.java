package com.sitare.payload.request;

import com.sitare.domain.UserRole;
import lombok.Data;

@Data
public class SignupDto {
	private String email;
	private String password;
	private String phone;
	private String fullName;
	private String username;
	private UserRole role;


}