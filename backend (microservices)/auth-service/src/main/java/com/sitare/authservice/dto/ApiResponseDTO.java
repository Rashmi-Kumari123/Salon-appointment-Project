package com.sitare.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO {
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Object data;

    public ApiResponseDTO(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponseDTO(String message, Object data) {
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
