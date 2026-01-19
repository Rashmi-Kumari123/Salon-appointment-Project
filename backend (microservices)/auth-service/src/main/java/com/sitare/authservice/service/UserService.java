package com.sitare.authservice.service;

import com.sitare.authservice.dto.SignupRequestDTO;
import com.sitare.authservice.model.User;
import com.sitare.authservice.repository.UserRepository;
import com.sitare.domain.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User insertNewUser(SignupRequestDTO request) {
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setPhone(request.getPhone());
        newUser.setFullName(request.getFullName());
        newUser.setUsername(request.getUsername());
        newUser.setRole(request.getRole() != null ? request.getRole() : UserRole.CUSTOMER);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(newUser);
    }
}
