package com.university.club.events.repository;

import com.university.club.events.entity.Event;
import com.university.club.events.entity.EventStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findAllByStatusOrderByStartAtAsc(EventStatus status);

    List<Event> findAllByOrderByStartAtDesc();

    @EntityGraph(attributePaths = {"createdBy"})
    Optional<Event> findWithCreatorById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") UUID id);

    long countByStatus(EventStatus status);

    List<Event> findAllByStatusAndStartAtAfterOrderByStartAtAsc(
            EventStatus status,
            LocalDateTime now
    );

    @Query("""
    SELECT e FROM Event e
    WHERE e.status IN :statuses
    AND e.endAt < :now
    ORDER BY e.endAt DESC
""")
    List<Event> findPastEvents(
            @Param("statuses") Collection<EventStatus> statuses,
            @Param("now") LocalDateTime now
    );

    @Query("""
    SELECT e FROM Event e
    WHERE e.status = :status
    AND (
        LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(COALESCE(e.location, '')) LIKE LOWER(CONCAT('%', :query, '%'))
    )
    ORDER BY e.startAt ASC
""")
    Page<Event> searchEvents(
            @Param("status") EventStatus status,
            @Param("query") String query,
            Pageable pageable
    );
}