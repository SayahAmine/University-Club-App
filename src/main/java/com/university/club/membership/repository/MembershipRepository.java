package com.university.club.membership.repository;

import com.university.club.membership.entity.Membership;
import com.university.club.membership.entity.MembershipStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository
        extends JpaRepository<Membership, UUID> {

    boolean existsByUser_Id(UUID userId);

    boolean existsByRoles_Name(String name);

    @EntityGraph(attributePaths = {
            "user",
            "roles",
            "roles.permissions"
    })
    Optional<Membership> findByUser_Id(UUID userId);

    @EntityGraph(attributePaths = {
            "user",
            "roles",
            "roles.permissions"
    })
    Optional<Membership> findById(UUID id);

    @EntityGraph(attributePaths = {
            "user",
            "roles",
            "roles.permissions"
    })
    List<Membership> findAllByStatusOrderByJoinedAtDesc(
            MembershipStatus status
    );

    @EntityGraph(attributePaths = {
            "user",
            "roles",
            "roles.permissions"
    })
    List<Membership> findAllByOrderByJoinedAtDesc();
}