package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.model.User;

public interface UserService {

	User addUser(User user);
	
    Optional<User> getById(int id);
    
    Optional<User> getByUsername(String username);
    
    List<User> getAllUsers();
    
    User updateUser(int id, User user);
    
    User deleteById(int id);
}
