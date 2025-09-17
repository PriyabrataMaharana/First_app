package com.example.mapper;

import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.model.User;

public class UserMapper {

    // Convert Request DTO -> Entity
    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setCity(dto.getCity());
        return user;
    }

    // Convert Entity -> Response DTO
    public static UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setCity(user.getCity());
        dto.setRole(user.getRole());
        return dto;
    }
}
