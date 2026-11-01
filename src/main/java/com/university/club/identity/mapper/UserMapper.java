package com.university.club.identity.mapper;

import com.university.club.identity.dto.UserResponse;
import com.university.club.identity.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getStudentNumber(),
                user.getAccountStatus().name(),
                user.getCreatedAt()
        );
    }
}