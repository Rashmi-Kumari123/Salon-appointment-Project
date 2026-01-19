package com.sitare.authservice.service;

import com.sitare.authservice.dto.*;
import com.sitare.authservice.model.User;
import com.sitare.authservice.repository.UserRepository;
import com.sitare.authservice.service.JwtService;
import com.sitare.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Optional<LoginResponseDTO> authenticate(LoginRequestDTO loginRequestDTO) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequestDTO.getEmail());
        
        if (userOpt.isEmpty() || !passwordEncoder.matches(loginRequestDTO.getPassword(), userOpt.get().getPassword())) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // Generate tokens
        String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRole(), user.getId());
        
        // Build claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());
        
        UserInfoDTO userInfo = new UserInfoDTO(user.getId(), user.getEmail(), user.getFullName(), user.getRole());
        
        return Optional.of(new LoginResponseDTO(
            token,
            refreshToken,
            jwtService.getJwtExpiration() / 1000, // Convert to seconds
            claims,
            userInfo
        ));
    }

    public boolean validateToken(String token) {
        try {
            return !jwtService.isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Optional<SignupResponseDTO> registerAndAuthenticate(SignupRequestDTO request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return Optional.empty();
        }

        // Create new user
        User newUser = userService.insertNewUser(request);

        // Generate tokens
        String token = jwtService.generateToken(newUser.getEmail(), newUser.getRole(), newUser.getId());
        String refreshToken = jwtService.generateRefreshToken(newUser.getEmail(), newUser.getRole(), newUser.getId());

        // Build claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", newUser.getEmail());
        claims.put("role", newUser.getRole().name());
        claims.put("userId", newUser.getId());

        UserInfoDTO userInfo = new UserInfoDTO(newUser.getId(), newUser.getEmail(), newUser.getFullName(), newUser.getRole());

        return Optional.of(new SignupResponseDTO(
            token,
            refreshToken,
            jwtService.getJwtExpiration() / 1000, // Convert to seconds
            claims,
            userInfo
        ));
    }

    @Transactional
    public Optional<LoginResponseDTO> refreshToken(RefreshTokenRequestDTO request) {
        String refreshTokenValue = request.getRefreshToken();

        // Validate refresh token
        if (jwtService.isTokenExpired(refreshTokenValue) || !jwtService.isRefreshToken(refreshTokenValue)) {
            return Optional.empty();
        }

        // Extract user info from refresh token
        String email = jwtService.extractEmail(refreshTokenValue);
        UserRole role = jwtService.extractRole(refreshTokenValue);
        Long userId = jwtService.extractUserId(refreshTokenValue);

        // Get user from database
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        // Generate new tokens
        String newToken = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRole(), user.getId());

        // Build claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());

        UserInfoDTO userInfo = new UserInfoDTO(user.getId(), user.getEmail(), user.getFullName(), user.getRole());

        return Optional.of(new LoginResponseDTO(
            newToken,
            newRefreshToken,
            jwtService.getJwtExpiration() / 1000,
            claims,
            userInfo
        ));
    }

    @Transactional
    public boolean logout(Long userId) {
        // For now, just return true. Can implement token revocation later if needed
        // TODO: Implement refresh token revocation using RefreshTokenRepository
        return true;
    }

    // Placeholder methods for advanced features - can be implemented later
    public boolean requestPasswordReset(PasswordResetRequestDTO request) {
        // TODO: Implement password reset
        return true;
    }

    public boolean resetPassword(PasswordResetDTO request) {
        // TODO: Implement password reset
        return false;
    }

    public boolean verifyEmail(EmailVerificationDTO request) {
        // TODO: Implement email verification
        return false;
    }

    public boolean resendVerificationEmail(Long userId) {
        // TODO: Implement resend verification email
        return false;
    }

    public boolean updateEmail(Long userId, String newEmail) {
        // TODO: Implement email update
        return false;
    }
}
