package com.university.club.membership.entity;

import com.university.club.identity.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "membership_applications")
@Getter
@Setter
public class MembershipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String motivation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public MembershipApplication() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = ApplicationStatus.PENDING;
        }
    }

    public void review(ApplicationStatus status, User reviewer) {
        if (this.status != ApplicationStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending applications can be reviewed."
            );
        }

        if (status != ApplicationStatus.APPROVED
                && status != ApplicationStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Invalid review status."
            );
        }

        this.status = status;
        this.reviewedBy = reviewer;
        this.reviewedAt = LocalDateTime.now();
    }
}