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
	public User updateUser(int id, User user) {
		User existingUser = repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        existingUser.setName(user.getName());
        existingUser.setUsername(user.getUsername());

        // encode only if a new password is provided
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setCity(user.getCity());
        return repo.save(existingUser);
	}

	@Override
	public User deleteById(int id) {
		User existingUser = repo.findById(id)
	            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

	    repo.delete(existingUser);
	    return existingUser;
	}

	@Override
	public List<User> getAllUsers() {
		return repo.findAll();
	}

}
