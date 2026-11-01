package com.university.club.events.service;

import com.university.club.common.exception.EventNotFoundException;
import com.university.club.events.dto.RegistrationResponse;
import com.university.club.events.dto.RegisterForEventRequest;
import com.university.club.events.entity.*;
import com.university.club.events.exception.InvalidEventStateException;
import com.university.club.events.exception.RegistrationNotFoundException;
import com.university.club.events.repository.*;
import com.university.club.identity.entity.User;
import com.university.club.identity.entity.UserStatus;
import com.university.club.identity.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventRegistrationService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public EventRegistrationService(
            EventRepository eventRepository,
            EventRegistrationRepository registrationRepository,
            UserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public RegistrationResponse register(
            UUID eventId,
            UUID userId,
            RegisterForEventRequest request
    ) {

        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found: " + eventId
                ));

        User user = findActiveUser(userId);

        validateRegistrationWindow(event);

        if (registrationRepository.existsByEvent_IdAndUser_Id(
                eventId, userId)) {
            throw new IllegalArgumentException(
                    "You have already registered for this event."
            );
        }

        if (!event.isRequiresApproval()) {
            ensureCapacityAvailable(event);
        }

        EventRegistration registration =
                EventRegistration.create(event, user);

        registration.updateNotes(
                request == null ? null : request.notes()
        );

        try {
            EventRegistration saved =
                    registrationRepository.saveAndFlush(registration);

            return toResponse(saved);

        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "You have already registered for this event."
            );
        }
    }


    @Transactional(readOnly = true)
    public List<RegistrationResponse> getMyRegistrations(UUID userId) {

        return registrationRepository
                .findAllByUser_IdOrderByRegisteredAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }



    @Transactional(readOnly = true)
    public List<RegistrationResponse> getEventRegistrations(
            UUID eventId
    ) {

        ensureEventExists(eventId);

        return registrationRepository
                .findAllByEvent_IdOrderByRegisteredAtAsc(eventId)
                .stream()
                .map(this::toResponse)
                .toList();
    }



    @Transactional(readOnly = true)
    public List<RegistrationResponse> getPendingRegistrations(
            UUID eventId
    ) {

        ensureEventExists(eventId);

        return registrationRepository
                .findAllByEvent_IdAndStatusOrderByRegisteredAtAsc(
                        eventId,
                        RegistrationStatus.PENDING
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RegistrationResponse approve(
            UUID registrationId,
            UUID reviewerId
    ) {

        EventRegistration registration =
                registrationRepository.findByIdForUpdate(registrationId)
                        .orElseThrow(() ->
                                new RegistrationNotFoundException(
                                        "Registration not found: "
                                                + registrationId
                                ));

        Event event = eventRepository.findByIdForUpdate(
                registration.getEvent().getId()
        ).orElseThrow(() -> new EventNotFoundException(
                "Event not found."
        ));

        User reviewer = findActiveUser(reviewerId);

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new InvalidEventStateException(
                    "Only published events can have registrations approved."
            );
        }

        validateRegistrationWindow(event);

        ensureCapacityAvailable(event);

        registration.approve(reviewer);

        return toResponse(registration);
    }


    @Transactional
    public RegistrationResponse reject(
            UUID registrationId,
            UUID reviewerId
    ) {

        EventRegistration registration =
                registrationRepository.findByIdForUpdate(registrationId)
                        .orElseThrow(() ->
                                new RegistrationNotFoundException(
                                        "Registration not found: "
                                                + registrationId
                                ));

        User reviewer = findActiveUser(reviewerId);

        registration.reject(reviewer);

        return toResponse(registration);
    }


    @Transactional
    public RegistrationResponse cancelMyRegistration(
            UUID registrationId,
            UUID userId
    ) {

        EventRegistration registration =
                registrationRepository.findByIdForUpdate(registrationId)
                        .orElseThrow(() ->
                                new RegistrationNotFoundException(
                                        "Registration not found: "
                                                + registrationId
                                ));

        if (!registration.getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only cancel your own registration."
            );
        }

        registration.cancel();

        return toResponse(registration);
    }

    // HELPERS


    private void validateRegistrationWindow(Event event) {

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new InvalidEventStateException(
                    "Registration is only available for published events."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (!event.getStartAt().isAfter(now)) {
            throw new InvalidEventStateException(
                    "Registration is closed because the event has started."
            );
        }

        if (event.getRegistrationDeadline() != null &&
                now.isAfter(event.getRegistrationDeadline())) {
            throw new InvalidEventStateException(
                    "The registration deadline has passed."
            );
        }
    }

    private void ensureCapacityAvailable(Event event) {

        if (event.getCapacity() == null) {
            return;
        }

        long approvedCount =
                registrationRepository.countByEvent_IdAndStatus(
                        event.getId(),
                        RegistrationStatus.APPROVED
                );

        if (approvedCount >= event.getCapacity()) {
            throw new InvalidEventStateException(
                    "This event has reached its capacity."
            );
        }
    }

    private User findActiveUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + userId
                ));

        if (user.getAccountStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Your account is not active."
            );
        }

        return user;
    }

    private void ensureEventExists(UUID eventId) {

        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(
                    "Event not found: " + eventId
            );
        }
    }

    private RegistrationResponse toResponse(
            EventRegistration registration
    ) {

        User user = registration.getUser();
        Event event = registration.getEvent();

        return new RegistrationResponse(
                registration.getId(),
                event.getId(),
                event.getTitle(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                registration.getStatus(),
                registration.getAttendanceStatus(),
                registration.getRegisteredAt(),
                registration.getReviewedBy() == null
                        ? null
                        : registration.getReviewedBy().getId(),
                registration.getReviewedAt(),
                registration.getAttendanceMarkedAt(),
                registration.getNotes()
        );
    }
}