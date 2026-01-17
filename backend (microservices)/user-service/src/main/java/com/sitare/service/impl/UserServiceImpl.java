package com.sitare.service.impl;
import com.sitare.exception.UserException;

import com.sitare.modal.User;

import com.sitare.repository.UserRepository;

import com.sitare.service.JwtService;
import com.sitare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final JwtService jwtService;

	@Override
	public User getUserByEmail(String email) throws UserException {
		User user=userRepository.findByEmail(email);
		if(user==null){
			throw new UserException("User not found with email: "+email);
		}
		return user;
	}

	@Override
	public User getUserFromJwtToken(String jwt) throws Exception {
		// Remove "Bearer " prefix if present
		if (jwt != null && jwt.startsWith("Bearer ")) {
			jwt = jwt.substring(7);
		}
		
		// Extract email from JWT token
		String email = jwtService.extractEmail(jwt);
		
		// Validate token
		if (jwtService.isTokenExpired(jwt)) {
			throw new Exception("Token has expired");
		}
		
		// Get user from database
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UserException("User not found with email: " + email);
		}
		return user;
	}

	@Override
	public User getUserById(Long id) throws UserException {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}


}
