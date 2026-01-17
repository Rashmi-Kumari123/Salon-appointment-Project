package com.sitare.service;
import com.sitare.payload.request.SignupDto;
import com.sitare.payload.response.AuthResponse;

public interface AuthService {
    AuthResponse login(String username, String password) throws Exception;
    AuthResponse signup(SignupDto req) throws Exception;
    AuthResponse getAccessTokenFromRefreshToken(String refreshToken) throws Exception;
}
