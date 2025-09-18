package com.example.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.example.model.Role;
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
	public ResponseEntity<?> getUserById(@PathVariable int id, Authentication authentication) {
		String principalUsername = authentication.getPrincipal().toString();

		// logged-in user
		User loggedInUser = userService.getByUsername(principalUsername)
				.orElseThrow(() -> new RuntimeException("Logged-in user not found"));

		// requested user
		User requestedUser = userService.getById(id)
				.orElseThrow(() -> new RuntimeException("Requested user not found"));

		// allow if ADMIN OR self
		if (!Role.ADMIN.equals(loggedInUser.getRole()) && loggedInUser.getId() != requestedUser.getId()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Access Denied: You can only view your own details"));
		}

		return ResponseEntity.ok(UserMapper.toResponse(requestedUser));
	}

	// ✅ Get User by Username
	@GetMapping("/username/{username}")
	public ResponseEntity<?> getUserByUsername(@PathVariable String username, Authentication authentication) {
		String principalUsername = authentication.getPrincipal().toString();

		// logged-in user
		User loggedInUser = userService.getByUsername(principalUsername)
				.orElseThrow(() -> new RuntimeException("Logged-in user not found"));

		// requested user
		User requestedUser = userService.getByUsername(username)
				.orElseThrow(() -> new RuntimeException("Requested user not found"));

		// allow if ADMIN OR self
		if (!Role.ADMIN.equals(loggedInUser.getRole()) && !loggedInUser.getUsername().equals(username)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Access Denied: You can only view your own details"));
		}

		return ResponseEntity.ok(UserMapper.toResponse(requestedUser));
	}

	@GetMapping
	public ResponseEntity<?> getAllUsers(Authentication authentication) {
		String username = authentication.getPrincipal().toString();
		var loggedInUser = userService.getByUsername(username)
				.orElseThrow(() -> new RuntimeException("Logged-in user not found"));

		if (!Role.ADMIN.equals(loggedInUser.getRole())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Access Denied: Only admins can view all users"));
		}

		List<UserResponseDTO> users = userService.getAllUsers().stream().map(UserMapper::toResponse).toList();

		return ResponseEntity.ok(users);
	}

	// ✅ Update User
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable int id, @Valid @RequestBody UserRequestDTO updatedUser,
			Authentication authentication) {

		String principalUsername = authentication.getPrincipal().toString();
		User loggedInUser = userService.getByUsername(principalUsername)
				.orElseThrow(() -> new RuntimeException("Logged-in user not found"));

		try {
			User user = userService.updateUser(id, UserMapper.toEntity(updatedUser), loggedInUser);
			return ResponseEntity.ok(UserMapper.toResponse(user));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		}
	}

	// ✅ Delete User
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable int id, Authentication authentication) {
		String principalUsername = authentication.getPrincipal().toString();
		User loggedInUser = userService.getByUsername(principalUsername)
				.orElseThrow(() -> new RuntimeException("Logged-in user not found"));

		try {
			userService.deleteById(id, loggedInUser);
			return ResponseEntity.ok("User deleted successfully!");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		}
	}
}
