package com.university.club.identity.service;

import com.university.club.common.exception.ResourceNotFoundException;
import com.university.club.identity.dto.UserResponse;
import com.university.club.identity.entity.User;
import com.university.club.identity.mapper.UserMapper;
import com.university.club.identity.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return UserMapper.toResponse(user);
    }
}