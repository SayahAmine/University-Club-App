package com.university.club.membership.repository;

import com.university.club.membership.entity.ApplicationStatus;
import com.university.club.membership.entity.MembershipApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipApplicationRepository
        extends JpaRepository<MembershipApplication, UUID> {

    boolean existsByUser_IdAndStatus(
            UUID userId,
            ApplicationStatus status
    );

    Optional<MembershipApplication> findByUser_IdAndStatus(
            UUID userId,
            ApplicationStatus status
    );

    @EntityGraph(attributePaths = {"user", "reviewedBy"})
    List<MembershipApplication> findAllByStatusOrderByCreatedAtAsc(
            ApplicationStatus status
    );

    @EntityGraph(attributePaths = {"user", "reviewedBy"})
    List<MembershipApplication> findByUser_IdOrderByCreatedAtDesc(
            UUID userId
    );
}