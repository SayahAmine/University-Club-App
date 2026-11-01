package com.university.club.events.service;

import com.university.club.common.exception.EventNotFoundException;
import com.university.club.events.exception.InvalidEventStateException;
import com.university.club.identity.entity.User;
import com.university.club.identity.repository.UserRepository;
import com.university.club.events.dto.CreateEventRequest;
import com.university.club.events.dto.UpdateEventRequest;
import com.university.club.events.dto.EventResponse;
import com.university.club.events.entity.Event;
import com.university.club.events.entity.EventStatus;
import com.university.club.events.repository.EventRepository;
import com.university.club.events.repository.EventRegistrationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public EventService(
            EventRepository eventRepository,
            EventRegistrationRepository registrationRepository,
            UserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EventResponse createEvent(
            CreateEventRequest request,
            UUID creatorId
    ) {

        validateDates(
                request.startAt(),
                request.endAt(),
                request.registrationDeadline()
        );

        User creator = findUser(creatorId);

        Event event = Event.create(
                request.title().trim(),
                request.description(),
                request.location(),
                request.startAt(),
                request.endAt(),
                request.registrationDeadline(),
                request.capacity(),
                request.requiresApproval(),
                creator
        );

        return toResponse(eventRepository.save(event));
    }


    @Transactional
    public EventResponse updateEvent(
            UUID eventId,
            UpdateEventRequest request
    ) {

        Event event = findEvent(eventId);

        if (event.getStatus() != EventStatus.DRAFT &&
                event.getStatus() != EventStatus.PUBLISHED) {

            throw new InvalidEventStateException(
                    "Only draft or published events can be updated."
            );
        }

        validateDates(
                request.startAt(),
                request.endAt(),
                request.registrationDeadline()
        );

        long approvedCount =
                registrationRepository.countByEvent_IdAndStatus(
                        eventId,
                        com.university.club.events.entity.RegistrationStatus.APPROVED
                );

        if (request.capacity() != null &&
                request.capacity() < approvedCount) {

            throw new InvalidEventStateException(
                    "Capacity cannot be lower than the number of approved registrations."
            );
        }

        event.updateDetails(
                request.title().trim(),
                request.description(),
                request.location(),
                request.startAt(),
                request.endAt(),
                request.registrationDeadline(),
                request.capacity(),
                request.requiresApproval()
        );

        return toResponse(event);
    }


    @Transactional
    public EventResponse publishEvent(UUID eventId) {

        Event event = findEvent(eventId);

        validateDates(
                event.getStartAt(),
                event.getEndAt(),
                event.getRegistrationDeadline()
        );

        if (!event.getStartAt().isAfter(LocalDateTime.now())) {
            throw new InvalidEventStateException(
                    "An event must start in the future."
            );
        }

        event.publish();

        return toResponse(event);
    }

    @Transactional
    public EventResponse cancelEvent(UUID eventId) {

        Event event = findEvent(eventId);

        event.cancel();

        return toResponse(event);
    }


    @Transactional
    public EventResponse completeEvent(UUID eventId) {

        Event event = findEvent(eventId);

        if (event.getEndAt().isAfter(LocalDateTime.now())) {
            throw new InvalidEventStateException(
                    "An event cannot be completed before its end time."
            );
        }

        event.complete();

        return toResponse(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getPublishedEvents() {

        return eventRepository
                .findAllByStatusOrderByStartAtAsc(EventStatus.PUBLISHED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {

        return eventRepository
                .findAllByOrderByStartAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public EventResponse getEvent(UUID eventId) {

        return toResponse(findEvent(eventId));
    }

    // HELPERS

    private Event findEvent(UUID eventId) {

        return eventRepository.findWithCreatorById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found: " + eventId
                ));
    }

    private User findUser(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + userId
                ));
    }

    private void validateDates(
            LocalDateTime startAt,
            LocalDateTime endAt,
            LocalDateTime registrationDeadline
    ) {

        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException(
                    "Event end time must be after start time."
            );
        }

        if (registrationDeadline != null &&
                registrationDeadline.isAfter(startAt)) {

            throw new IllegalArgumentException(
                    "Registration deadline cannot be after event start."
            );
        }
    }

    private EventResponse toResponse(Event event) {

        User creator = event.getCreatedBy();

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartAt(),
                event.getEndAt(),
                event.getRegistrationDeadline(),
                event.getCapacity(),
                event.isRequiresApproval(),
                event.getStatus(),
                creator.getId(),
                creator.getFirstName() + " " + creator.getLastName(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}