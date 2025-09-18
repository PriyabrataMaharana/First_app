package com.example.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.exception.UserNotFoundException;
import com.example.model.Role;
import com.example.model.User;
import com.example.repo.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository repo;
	private final PasswordEncoder passwordEncoder;

	// Constructor Injection
	public UserServiceImpl(UserRepository repo, PasswordEncoder passwordEncoder) {
		this.repo = repo;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User addUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (user.getRole() == null) {
			user.setRole(Role.USER); // default role
		}
		return repo.save(user);
	}

	@Override
	public Optional<User> getById(int id) {
		return repo.findById(id);
	}

	@Override
	public Optional<User> getByUsername(String username) {
		return repo.findByUsername(username);
	}

	@Override
	public User updateUser(int id, User updatedUser, User currentUser) {
		User existingUser = repo.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		// Allow only ADMIN or self
		if (!Role.ADMIN.equals(currentUser.getRole()) && currentUser.getId() != id) {
			throw new RuntimeException("Access denied: You can only update your own account");
		}

		// ✅ Updatable fields
		existingUser.setName(updatedUser.getName());
		existingUser.setUsername(updatedUser.getUsername());
		existingUser.setCity(updatedUser.getCity());

		// ✅ Password (encode if provided)
		if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
			existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		}

		// ✅ Role only changeable by ADMIN
		if (Role.ADMIN.equals(currentUser.getRole()) && updatedUser.getRole() != null) {
			existingUser.setRole(updatedUser.getRole());
		}

		return repo.save(existingUser);
	}

	@Override
	public void deleteById(int id, User currentUser) {
		User existingUser = repo.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		// Allow only ADMIN or self
		if (!Role.ADMIN.equals(currentUser.getRole()) && currentUser.getId() != id) {
			throw new RuntimeException("Access denied: You can only delete your own account");
		}

		repo.delete(existingUser);
	}

	@Override
	public List<User> getAllUsers() {
		return repo.findAll();
	}

}
