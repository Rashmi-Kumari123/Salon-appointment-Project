package com.sitare.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateEmailRequestDTO {
    @NotBlank(message = "New email is required")
    @Email(message = "New email should be a valid email address")
    private String newEmail;

    public UpdateEmailRequestDTO() {
    }

    public UpdateEmailRequestDTO(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
