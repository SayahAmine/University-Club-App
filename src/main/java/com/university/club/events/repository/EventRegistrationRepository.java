package com.university.club.events.repository;

import com.university.club.events.entity.AttendanceStatus;
import com.university.club.events.entity.EventRegistration;
import com.university.club.events.entity.RegistrationStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRegistrationRepository
        extends JpaRepository<EventRegistration, UUID> {

    boolean existsByEvent_IdAndUser_Id(UUID eventId, UUID userId);

    long countByEvent_IdAndStatusAndAttendanceStatus(
            UUID eventId,
            RegistrationStatus status,
            AttendanceStatus attendanceStatus
    );

    Optional<EventRegistration> findByEvent_IdAndUser_Id(
            UUID eventId,
            UUID userId
    );

    @EntityGraph(attributePaths = {"user", "event"})
    List<EventRegistration> findAllByEvent_IdOrderByRegisteredAtAsc(
            UUID eventId
    );

    @EntityGraph(attributePaths = {"user", "event"})
    List<EventRegistration> findAllByEvent_IdAndStatusOrderByRegisteredAtAsc(
            UUID eventId,
            RegistrationStatus status
    );

    @EntityGraph(attributePaths = {"event"})
    List<EventRegistration> findAllByUser_IdOrderByRegisteredAtDesc(
            UUID userId
    );

    long countByEvent_IdAndStatus(
            UUID eventId,
            RegistrationStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r
        FROM EventRegistration r
        WHERE r.id = :id
    """)
    Optional<EventRegistration> findByIdForUpdate(
            @Param("id") UUID id
    );

    long countByStatus(RegistrationStatus status);


}