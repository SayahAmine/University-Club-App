package com.university.club.events.entity;

import com.university.club.identity.entity.User;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 300)
    private String location;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;

    private Integer capacity;

    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventStatus status = EventStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Event() {
    }

    public static Event create(
            String title,
            String description,
            String location,
            LocalDateTime startAt,
            LocalDateTime endAt,
            LocalDateTime registrationDeadline,
            Integer capacity,
            boolean requiresApproval,
            User createdBy
    ) {
        Event event = new Event();

        event.title = title;
        event.description = description;
        event.location = location;
        event.startAt = startAt;
        event.endAt = endAt;
        event.registrationDeadline = registrationDeadline;
        event.capacity = capacity;
        event.requiresApproval = requiresApproval;
        event.createdBy = createdBy;
        event.status = EventStatus.DRAFT;

        return event;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = EventStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateDetails(
            String title,
            String description,
            String location,
            LocalDateTime startAt,
            LocalDateTime endAt,
            LocalDateTime registrationDeadline,
            Integer capacity,
            boolean requiresApproval
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startAt = startAt;
        this.endAt = endAt;
        this.registrationDeadline = registrationDeadline;
        this.capacity = capacity;
        this.requiresApproval = requiresApproval;
    }

    public void publish() {
        if (status != EventStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only draft events can be published."
            );
        }

        status = EventStatus.PUBLISHED;
    }

    public void cancel() {
        if (status == EventStatus.COMPLETED ||
                status == EventStatus.CANCELLED) {
            throw new IllegalStateException(
                    "This event cannot be cancelled."
            );
        }

        status = EventStatus.CANCELLED;
    }

    public void complete() {
        if (status != EventStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Only published events can be completed."
            );
        }

        status = EventStatus.COMPLETED;
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public boolean isRequiresApproval() {
        return requiresApproval;
    }

    public EventStatus getStatus() {
        return status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}