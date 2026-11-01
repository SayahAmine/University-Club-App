package com.university.club.membership.service;

import com.university.club.common.security.UserAuthorityProvider;
import com.university.club.membership.entity.Membership;
import com.university.club.membership.entity.MembershipStatus;
import com.university.club.membership.repository.MembershipRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/*
1. Finds the user's membership.
2. Checks whether it is active.
3. Loads the roles assigned to that membership.
4. Collects the permissions from those roles.
5. Converts permission codes into Spring Security authorities.
 */

@Service
public class MembershipAuthorityProvider implements UserAuthorityProvider {

    private final MembershipRepository membershipRepository;

    public MembershipAuthorityProvider(
            MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<? extends GrantedAuthority> getAuthorities(UUID userId) {

        return membershipRepository.findByUser_Id(userId)
                .filter(membership ->
                        membership.getStatus() == MembershipStatus.ACTIVE)
                .map(this::extractAuthorities)
                .orElse(Set.of());
    }

    private Set<GrantedAuthority> extractAuthorities(
            Membership membership) {

        return membership.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission ->
                        new SimpleGrantedAuthority(permission.getCode()))
                .collect(Collectors.toSet());
    }
}