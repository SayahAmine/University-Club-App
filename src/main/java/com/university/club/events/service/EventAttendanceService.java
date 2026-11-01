package com.university.club.events.service;

import com.university.club.common.exception.EventNotFoundException;
import com.university.club.events.dto.AttendanceRequest;
import com.university.club.events.dto.AttendanceSummaryResponse;
import com.university.club.events.dto.RegistrationResponse;
import com.university.club.events.entity.*;
import com.university.club.events.exception.InvalidEventStateException;
import com.university.club.events.exception.RegistrationNotFoundException;
import com.university.club.events.repository.EventRepository;
import com.university.club.events.repository.EventRegistrationRepository;
import com.university.club.identity.entity.User;
import com.university.club.identity.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventAttendanceService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public EventAttendanceService(
            EventRepository eventRepository,
            EventRegistrationRepository registrationRepository,
            UserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }



    @Transactional
    public RegistrationResponse markAttendance(
            UUID registrationId,
            AttendanceRequest request,
            UUID managerId
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

        if (event.getStatus() != EventStatus.PUBLISHED &&
                event.getStatus() != EventStatus.COMPLETED) {

            throw new InvalidEventStateException(
                    "Attendance can only be recorded for published or completed events."
            );
        }

        if (event.getStartAt().isAfter(LocalDateTime.now())) {
            throw new InvalidEventStateException(
                    "Attendance cannot be recorded before the event starts."
            );
        }

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Manager not found."
                ));

        if (request.status() == AttendanceStatus.NOT_MARKED) {
            throw new IllegalArgumentException(
                    "Attendance must be PRESENT or ABSENT."
            );
        }

        registration.markAttendance(
                request.status(),
                manager
        );

        return toResponse(registration);
    }



    @Transactional(readOnly = true)
    public List<RegistrationResponse> getAttendance(UUID eventId) {

        ensureEventExists(eventId);

        return registrationRepository
                .findAllByEvent_IdOrderByRegisteredAtAsc(eventId)
                .stream()
                .filter(registration ->
                        registration.getStatus() ==
                                RegistrationStatus.APPROVED
                )
                .map(this::toResponse)
                .toList();
    }



    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getAttendanceSummary(
            UUID eventId
    ) {

        Event event = eventRepository.findWithCreatorById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found: " + eventId
                ));

        long totalApproved =
                registrationRepository.countByEvent_IdAndStatus(
                        eventId,
                        RegistrationStatus.APPROVED
                );

        long present = registrationRepository
                .countByEvent_IdAndStatusAndAttendanceStatus(
                        eventId,
                        RegistrationStatus.APPROVED,
                        AttendanceStatus.PRESENT
                );

        long absent = registrationRepository
                .countByEvent_IdAndStatusAndAttendanceStatus(
                        eventId,
                        RegistrationStatus.APPROVED,
                        AttendanceStatus.ABSENT
                );

        long notMarked = totalApproved - present - absent;

        long marked = present + absent;

        double attendanceRate = marked == 0
                ? 0.0
                : (present * 100.0) / marked;

        return new AttendanceSummaryResponse(
                event.getId(),
                event.getTitle(),
                totalApproved,
                present,
                absent,
                notMarked,
                Math.round(attendanceRate * 100.0) / 100.0
        );
    }


    // HELPERS


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