package com.sitare.controller;

import com.sitare.exception.UserException;
import com.sitare.mapper.UserMapper;
import com.sitare.modal.User;

import com.sitare.payload.dto.UserDTO;
import com.sitare.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;

	@GetMapping("/api/users/profile")
	public ResponseEntity<UserDTO> getUserFromJwtToken(
			@RequestHeader(value = "X-User-Id", required = false) Long userId) throws Exception {

		// Gateway validates JWT and adds X-User-Id header
		// Use userId from header instead of parsing JWT token
		if (userId == null) {
			throw new UserException("User ID not found in request headers. Please ensure you are authenticated.");
		}
		
		User user = userService.getUserById(userId);
		if (user == null) {
			throw new UserException("User not found with ID: " + userId);
		}
		
		UserDTO userDTO = userMapper.mapToDTO(user);
		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

	@GetMapping("/api/users/{userId}")
	public ResponseEntity<UserDTO> getUserById(
			@PathVariable Long userId
	) throws UserException {
		User user = userService.getUserById(userId);
		if(user==null) {
			throw new UserException("User not found");
		}
		UserDTO userDTO=userMapper.mapToDTO(user);

		return new ResponseEntity<>(userDTO,HttpStatus.OK);
	}

	@GetMapping("/api/users")
	public ResponseEntity<List<User>> getUsers(
	) throws UserException {
		List<User> users = userService.getAllUsers();

		return new ResponseEntity<>(users,HttpStatus.OK);
	}






}
