package com.sitare.authservice.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDTO {
    private String token;
    private String refreshToken;
    private Long expiresIn;
    private Map<String, Object> claims;
    private UserInfoDTO user;
}
