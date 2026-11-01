package com.university.club.membership.service;

import com.university.club.identity.entity.User;
import com.university.club.identity.entity.UserStatus;
import com.university.club.identity.repository.UserRepository;

import com.university.club.membership.dto.ApplicationRequest;
import com.university.club.membership.dto.ApplicationResponse;

import com.university.club.membership.entity.*;
import com.university.club.membership.exception.*;

import com.university.club.membership.repository.*;

import com.university.club.common.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MembershipApplicationService {

    private final MembershipApplicationRepository applicationRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public MembershipApplicationService(
            MembershipApplicationRepository applicationRepository,
            MembershipRepository membershipRepository,
            UserRepository userRepository,
            RoleRepository roleRepository) {

        this.applicationRepository = applicationRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Student submits an application
    @Transactional
    public ApplicationResponse submitApplication(
            UUID userId,
            ApplicationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (user.getAccountStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only active accounts can apply for membership.");
        }

        if (membershipRepository.existsByUser_Id(userId)) {
            throw new MembershipAlreadyExistsException();
        }

        if (applicationRepository.existsByUser_IdAndStatus(
                userId, ApplicationStatus.PENDING)) {

            throw new PendingApplicationExistsException();
        }

        MembershipApplication application = new MembershipApplication();

        application.setUser(user);
        application.setMotivation(request.motivation().trim());

        MembershipApplication saved =
                applicationRepository.save(application);

        return toApplicationResponse(saved);
    }

    // Student views their application history
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(UUID userId) {

        return applicationRepository
                .findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toApplicationResponse)
                .toList();
    }

    // Administrators view pending applications
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getPendingApplications() {

        return applicationRepository
                .findAllByStatusOrderByCreatedAtAsc(
                        ApplicationStatus.PENDING)
                .stream()
                .map(this::toApplicationResponse)
                .toList();
    }

    // Administrator approves an application
    @Transactional
    public ApplicationResponse approveApplication(
            UUID applicationId,
            UUID reviewerId) {

        MembershipApplication application =
                findPendingApplication(applicationId);

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reviewer not found"));

        UUID applicantId = application.getUser().getId();

        if (membershipRepository.existsByUser_Id(applicantId)) {
            throw new MembershipAlreadyExistsException();
        }

        Role memberRole = roleRepository.findByNameIgnoreCase("MEMBER")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Default MEMBER role was not found."));

        // Create membership
        Membership membership = new Membership();
        membership.setUser(application.getUser());
        membership.setStatus(MembershipStatus.ACTIVE);
        membership.addRole(memberRole);

        membershipRepository.save(membership);

        // Mark application as approved
        application.review(ApplicationStatus.APPROVED, reviewer);

        MembershipApplication saved =
                applicationRepository.save(application);

        return toApplicationResponse(saved);
    }

    // Administrator rejects an application
    @Transactional
    public ApplicationResponse rejectApplication(
            UUID applicationId,
            UUID reviewerId) {

        MembershipApplication application =
                findPendingApplication(applicationId);

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reviewer not found"));

        application.review(ApplicationStatus.REJECTED, reviewer);

        MembershipApplication saved =
                applicationRepository.save(application);

        return toApplicationResponse(saved);
    }

    private MembershipApplication findPendingApplication(UUID applicationId) {

        MembershipApplication application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Membership application not found"));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new InvalidApplicationStateException(
                    "This application has already been reviewed.");
        }

        return application;
    }

    private ApplicationResponse toApplicationResponse(
            MembershipApplication application) {

        User user = application.getUser();
        User reviewer = application.getReviewedBy();

        return new ApplicationResponse(
                application.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getStudentNumber(),
                application.getMotivation(),
                application.getStatus(),
                reviewer == null
                        ? null
                        : reviewer.getFirstName() + " "
                        + reviewer.getLastName(),
                application.getReviewedAt(),
                application.getCreatedAt()
        );
    }
}