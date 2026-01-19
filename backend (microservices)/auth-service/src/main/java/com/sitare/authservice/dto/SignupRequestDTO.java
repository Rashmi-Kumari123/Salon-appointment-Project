package com.sitare.authservice.dto;

import com.sitare.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be a valid email address")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    private String phone;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    private UserRole role;
}
