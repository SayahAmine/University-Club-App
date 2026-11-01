package com.university.club.events.service;

import com.university.club.common.exception.EventNotFoundException;
import com.university.club.events.dto.EventResponse;
import com.university.club.events.dto.EventSummaryResponse;
import com.university.club.events.entity.Event;
import com.university.club.events.entity.EventStatus;
import com.university.club.events.entity.RegistrationStatus;
import com.university.club.events.repository.EventRegistrationRepository;
import com.university.club.events.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventQueryService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;

    public List<EventResponse> getUpcomingEvents() {

        LocalDateTime now = LocalDateTime.now();

        return eventRepository
                .findAllByStatusAndStartAtAfterOrderByStartAtAsc(
                        EventStatus.PUBLISHED,
                        now
                )
                .stream()
                .map(this::mapEvent)
                .toList();
    }

    public List<EventResponse> getPastEvents() {

        List<EventStatus> statuses = List.of(
                EventStatus.PUBLISHED,
                EventStatus.COMPLETED
        );

        return eventRepository.findPastEvents(
                        statuses,
                        LocalDateTime.now()
                )
                .stream()
                .map(this::mapEvent)
                .toList();
    }

    public Page<EventResponse> searchPublicEvents(
            String query,
            Pageable pageable
    ) {

        return eventRepository.searchEvents(
                EventStatus.PUBLISHED,
                query == null ? "" : query.trim(),
                pageable
        ).map(this::mapEvent);
    }

    public List<EventSummaryResponse> getEventSummaries() {

        return eventRepository.findAllByOrderByStartAtDesc()
                .stream()
                .map(this::mapSummary)
                .toList();
    }

    public EventSummaryResponse getEventSummary(UUID eventId) {

        Event event = eventRepository.findWithCreatorById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found")
                );

        return mapSummary(event);
    }

    private EventSummaryResponse mapSummary(Event event) {

        long approved = registrationRepository.countByEvent_IdAndStatus(
                event.getId(),
                RegistrationStatus.APPROVED
        );

        long pending = registrationRepository.countByEvent_IdAndStatus(
                event.getId(),
                RegistrationStatus.PENDING
        );

        Long remaining = event.getCapacity() == null
                ? null
                : Math.max(0, event.getCapacity() - approved);

        return new EventSummaryResponse(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getStartAt(),
                event.getEndAt(),
                event.getStatus(),
                event.getCapacity(),
                approved,
                pending,
                remaining,
                event.isRequiresApproval()
        );
    }

    private EventResponse mapEvent(Event event) {

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
                event.getCreatedBy().getId(),
                event.getCreatedBy().getFirstName() + " "
                        + event.getCreatedBy().getLastName(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}