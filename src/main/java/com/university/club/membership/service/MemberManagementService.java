package com.university.club.membership.service;

import com.university.club.common.exception.ResourceNotFoundException;
import com.university.club.identity.entity.User;
import com.university.club.membership.dto.MemberDetailsResponse;
import com.university.club.membership.entity.Membership;
import com.university.club.membership.entity.MembershipStatus;
import com.university.club.membership.repository.MembershipRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemberManagementService {

    private final MembershipRepository membershipRepository;

    public MemberManagementService(
            MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberDetailsResponse> getAllMembers() {

        return membershipRepository.findAllByOrderByJoinedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberDetailsResponse> getMembersByStatus(
            MembershipStatus status) {

        return membershipRepository
                .findAllByStatusOrderByJoinedAtDesc(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberDetailsResponse getMember(UUID membershipId) {

        Membership membership = findMembership(membershipId);

        return toResponse(membership);
    }

    @Transactional
    public MemberDetailsResponse suspendMembership(UUID membershipId) {

        Membership membership = findMembership(membershipId);

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only active memberships can be suspended."
            );
        }

        membership.setStatus(MembershipStatus.SUSPENDED);

        return toResponse(membership);
    }

    @Transactional
    public MemberDetailsResponse reactivateMembership(UUID membershipId) {

        Membership membership = findMembership(membershipId);

        if (membership.getStatus() != MembershipStatus.SUSPENDED) {
            throw new IllegalStateException(
                    "Only suspended memberships can be reactivated."
            );
        }

        membership.setStatus(MembershipStatus.ACTIVE);

        return toResponse(membership);
    }

    private Membership findMembership(UUID membershipId) {

        return membershipRepository.findById(membershipId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Membership not found"
                        )
                );
    }

    private MemberDetailsResponse toResponse(Membership membership) {

        User user = membership.getUser();

        Set<String> roles = membership.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        return new MemberDetailsResponse(
                membership.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getStudentNumber(),
                membership.getStatus(),
                roles,
                membership.getJoinedAt()
        );
    }
}