package com.university.club.common.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public interface UserAuthorityProvider {

    Collection<? extends GrantedAuthority> getAuthorities(UUID userId);
}