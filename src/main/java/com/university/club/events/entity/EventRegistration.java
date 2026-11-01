package com.university.club.events.entity;

import com.university.club.identity.entity.User;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "event_registrations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_event_user",
                        columnNames = {"event_id", "user_id"}
                )
        }
)
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false, length = 30)
    private AttendanceStatus attendanceStatus =
            AttendanceStatus.NOT_MARKED;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_marked_by")
    private User attendanceMarkedBy;

    @Column(name = "attendance_marked_at")
    private LocalDateTime attendanceMarkedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    protected EventRegistration() {
    }

    public static EventRegistration create(Event event, User user) {
        EventRegistration registration = new EventRegistration();

        registration.event = event;
        registration.user = user;
        registration.status = event.isRequiresApproval()
                ? RegistrationStatus.PENDING
                : RegistrationStatus.APPROVED;

        return registration;
    }

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }


    public void approve(User reviewer) {
        ensurePending();

        status = RegistrationStatus.APPROVED;
        reviewedBy = reviewer;
        reviewedAt = LocalDateTime.now();
    }

    public void reject(User reviewer) {
        ensurePending();

        status = RegistrationStatus.REJECTED;
        reviewedBy = reviewer;
        reviewedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status != RegistrationStatus.PENDING &&
                status != RegistrationStatus.APPROVED) {
            throw new IllegalStateException(
                    "This registration cannot be cancelled."
            );
        }

        status = RegistrationStatus.CANCELLED;
    }

    public void markAttendance(
            AttendanceStatus attendanceStatus,
            User markedBy
    ) {
        if (status != RegistrationStatus.APPROVED) {
            throw new IllegalStateException(
                    "Attendance can only be marked for approved registrations."
            );
        }

        if (attendanceStatus == null ||
                attendanceStatus == AttendanceStatus.NOT_MARKED) {
            throw new IllegalArgumentException(
                    "Select PRESENT or ABSENT."
            );
        }

        this.attendanceStatus = attendanceStatus;
        this.attendanceMarkedBy = markedBy;
        this.attendanceMarkedAt = LocalDateTime.now();
    }

    private void ensurePending() {
        if (status != RegistrationStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending registrations can be reviewed."
            );
        }
    }

    public void resetAttendance() {

        this.attendanceStatus = AttendanceStatus.NOT_MARKED;
        this.attendanceMarkedBy = null;
        this.attendanceMarkedAt = null;
    }

    public void updateNotes(String notes) {
        this.notes = notes;
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public User getAttendanceMarkedBy() {
        return attendanceMarkedBy;
    }

    public LocalDateTime getAttendanceMarkedAt() {
        return attendanceMarkedAt;
    }

    public String getNotes() {
        return notes;
    }
}