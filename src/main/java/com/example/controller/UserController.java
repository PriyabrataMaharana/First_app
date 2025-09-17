package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	// Constructor Injection
	public UserController(UserService userService) {
		this.userService = userService;
	}

	// ✅ Create User
	@PostMapping("/add")
	public ResponseEntity<UserResponseDTO> addUser(@Valid @RequestBody UserRequestDTO userRequest) {
		User savedUser = userService.addUser(UserMapper.toEntity(userRequest));
		return ResponseEntity.ok(UserMapper.toResponse(savedUser));
	}

	// ✅ Get User by ID
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable int id) {
	    return userService.getById(id)
	            .map(UserMapper::toResponse)
	            .map(ResponseEntity::ok)
	            .orElse(ResponseEntity.notFound().build());
	}

	// ✅ Get User by Username
	@GetMapping("/username/{username}")
	public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
		return userService.getByUsername(username).map(UserMapper::toResponse).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// ✅ Get All Users
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		List<UserResponseDTO> users = userService.getAllUsers().stream().map(UserMapper::toResponse).toList();
		return ResponseEntity.ok(users);
	}

	// ✅ Update User
	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable int id,
			@Valid @RequestBody UserRequestDTO updatedUser) {
		User user = userService.updateUser(id, UserMapper.toEntity(updatedUser));
		return ResponseEntity.ok(UserMapper.toResponse(user));
	}

	// ✅ Delete User
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable int id) {
		userService.deleteById(id);
		return ResponseEntity.ok("User deleted successfully!");
	}
}
