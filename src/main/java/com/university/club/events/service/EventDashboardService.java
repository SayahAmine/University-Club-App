package com.university.club.events.service;

import com.university.club.events.dto.EventDashboardResponse;
import com.university.club.events.entity.EventStatus;
import com.university.club.events.entity.RegistrationStatus;
import com.university.club.events.repository.EventRepository;
import com.university.club.events.repository.EventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventDashboardService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;

    public EventDashboardResponse getDashboard() {

        LocalDateTime now = LocalDateTime.now();

        long totalEvents = eventRepository.count();

        long drafts = eventRepository.countByStatus(EventStatus.DRAFT);

        long published = eventRepository.countByStatus(EventStatus.PUBLISHED);

        long upcoming = eventRepository
                .findAllByStatusAndStartAtAfterOrderByStartAtAsc(
                        EventStatus.PUBLISHED,
                        now
                ).size();

        long completed = eventRepository.countByStatus(EventStatus.COMPLETED);

        long cancelled = eventRepository.countByStatus(EventStatus.CANCELLED);

        long pendingRegistrations =
                registrationRepository.countByStatus(RegistrationStatus.PENDING);

        long approvedRegistrations =
                registrationRepository.countByStatus(RegistrationStatus.APPROVED);

        long totalRegistrations =
                registrationRepository.count();

        return new EventDashboardResponse(
                totalEvents,
                drafts,
                published,
                upcoming,
                completed,
                cancelled,
                totalRegistrations,
                pendingRegistrations,
                approvedRegistrations
        );
    }
}