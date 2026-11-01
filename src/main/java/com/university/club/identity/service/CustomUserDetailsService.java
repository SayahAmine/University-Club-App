package com.university.club.identity.service;

import com.university.club.common.security.UserAuthorityProvider;
import com.university.club.common.security.UserPrincipal;
import com.university.club.identity.entity.User;
import com.university.club.identity.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserAuthorityProvider authorityProvider;

    public CustomUserDetailsService(
            UserRepository userRepository,
            UserAuthorityProvider authorityProvider) {
        this.userRepository = userRepository;
        this.authorityProvider = authorityProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getAccountStatus(),
                authorityProvider.getAuthorities(user.getId())
        );
    }
}