package com.sitare.service.impl;
import com.sitare.exception.UserException;

import com.sitare.modal.User;

import com.sitare.repository.UserRepository;

import com.sitare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

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
		// JWT validation is handled by gateway/auth-service
		// This method is kept for backward compatibility but should be refactored
		// to accept user ID or email directly instead of JWT token
		throw new UnsupportedOperationException("JWT validation moved to auth-service. Use getUserById or getUserByEmail instead.");
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
