package com.sitare.service.impl;
import com.sitare.exception.UserException;
import com.sitare.modal.User;
import com.sitare.payload.request.SignupDto;
import com.sitare.payload.response.AuthResponse;
import com.sitare.repository.UserRepository;
import com.sitare.service.AuthService;
import com.sitare.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public AuthResponse signup(SignupDto req) throws Exception {
        // Check if user already exists
        if (userRepository.findByEmail(req.getEmail()) != null) {
            throw new UserException("User with email " + req.getEmail() + " already exists");
        }

        // Create new user
        User createdUser = new User();
        createdUser.setEmail(req.getEmail());
        createdUser.setPassword(passwordEncoder.encode(req.getPassword()));
        createdUser.setPhone(req.getPhone());
        createdUser.setRole(req.getRole() != null ? req.getRole() : com.sitare.domain.UserRole.CUSTOMER);
        createdUser.setFullName(req.getFullName());
        createdUser.setUsername(req.getUsername());
        createdUser.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(createdUser);

        // Generate JWT tokens
        String accessToken = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole(), savedUser.getId());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail(), savedUser.getRole(), savedUser.getId());

        AuthResponse response = new AuthResponse();
        response.setTitle("Welcome " + createdUser.getEmail());
        response.setMessage("Register success");
        response.setJwt(accessToken);
        response.setRefresh_token(refreshToken);
        return response;
    }

    @Override
    public AuthResponse getAccessTokenFromRefreshToken(String refreshToken) throws Exception {
        // Validate refresh token
        if (jwtService.isTokenExpired(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new Exception("Invalid or expired refresh token");
        }

        // Extract user info from refresh token
        String email = jwtService.extractEmail(refreshToken);
        com.sitare.domain.UserRole role = jwtService.extractRole(refreshToken);
        Long userId = jwtService.extractUserId(refreshToken);

        // Generate new access token
        String newAccessToken = jwtService.generateToken(email, role, userId);
        String newRefreshToken = jwtService.generateRefreshToken(email, role, userId);

        AuthResponse response = new AuthResponse();
        response.setMessage("Access token received");
        response.setJwt(newAccessToken);
        response.setRefresh_token(newRefreshToken);
        return response;
    }

    @Override
    public AuthResponse login(String username, String password) throws Exception {
        // Find user by email (username is email in this case)
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UserException("Invalid username or password");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException("Invalid username or password");
        }

        // Generate JWT tokens
        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRole(), user.getId());

        AuthResponse response = new AuthResponse();
        response.setTitle("Welcome Back " + username);
        response.setMessage("login success");
        response.setJwt(accessToken);
        response.setRefresh_token(refreshToken);
        return response;
    }
}
