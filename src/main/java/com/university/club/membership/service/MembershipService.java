package com.university.club.membership.service;

import com.university.club.identity.entity.User;
import com.university.club.membership.dto.MembershipResponse;
import com.university.club.membership.entity.Membership;
import com.university.club.membership.repository.MembershipRepository;
import com.university.club.common.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipService(
            MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public MembershipResponse getMyMembership(UUID userId) {

        Membership membership = membershipRepository
                .findByUser_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Membership not found"));

        User user = membership.getUser();

        return new MembershipResponse(
                membership.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                membership.getStatus(),
                membership.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()),
                membership.getJoinedAt()
        );
    }
}