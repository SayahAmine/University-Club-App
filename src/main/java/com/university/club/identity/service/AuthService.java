package com.university.club.identity.service;

import com.university.club.common.security.JwtService;
import com.university.club.common.security.UserPrincipal;

import com.university.club.identity.dto.AuthResponse;
import com.university.club.identity.dto.LoginRequest;
import com.university.club.identity.dto.RegisterRequest;
import com.university.club.identity.entity.User;
import com.university.club.identity.entity.UserStatus;

import com.university.club.identity.mapper.UserMapper;
import com.university.club.identity.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new com.university.club.common.exception
                    .EmailAlreadyExistsException(
                    "An account with this email already exists"
            );
        }

        String studentNumber = request.studentNumber();

        if (studentNumber != null) {
            studentNumber = studentNumber.trim();

            if (studentNumber.isEmpty()) {
                studentNumber = null;
            }
        }

        if (studentNumber != null &&
                userRepository.existsByStudentNumber(studentNumber)) {

            throw new IllegalArgumentException(
                    "Student number is already registered"
            );
        }

        User user = new User();

        user.setEmail(email);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setStudentNumber(studentNumber);

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        user.setAccountStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(savedUser);

        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                UserMapper.toResponse(savedUser)
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        User user = userRepository
                .findByEmailIgnoreCase(principal.getUsername())
                .orElseThrow();

        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                UserMapper.toResponse(user)
        );
    }
}