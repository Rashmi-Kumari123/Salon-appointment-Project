package com.sitare.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public class EmailVerificationDTO {
    @NotBlank(message = "Verification token is required")
    private String token;

    public EmailVerificationDTO() {
    }

    public EmailVerificationDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
