package com.university.club.inventory.entity;

import com.university.club.identity.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "borrow_requests",
        uniqueConstraints = {}
)
@Getter
@NoArgsConstructor
public class BorrowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String purpose;

    @Column(nullable = false)
    private LocalDateTime requestedFrom;

    @Column(nullable = false)
    private LocalDateTime requestedUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status = BorrowStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    private LocalDateTime borrowedAt;

    private LocalDateTime returnedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;

    public static BorrowRequest create(
            Asset asset,
            User user,
            String purpose,
            LocalDateTime requestedFrom,
            LocalDateTime requestedUntil
    ) {
        BorrowRequest request = new BorrowRequest();

        request.asset = asset;
        request.user = user;
        request.purpose = purpose;
        request.requestedFrom = requestedFrom;
        request.requestedUntil = requestedUntil;

        return request;
    }

    public void approve(User reviewer, String notes) {
        if (status != BorrowStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending requests can be approved"
            );
        }

        status = BorrowStatus.APPROVED;
        reviewedBy = reviewer;
        reviewedAt = LocalDateTime.now();
        this.notes = notes;
    }

    public void reject(User reviewer, String notes) {
        if (status != BorrowStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending requests can be rejected"
            );
        }

        status = BorrowStatus.REJECTED;
        reviewedBy = reviewer;
        reviewedAt = LocalDateTime.now();
        this.notes = notes;
    }

    public void cancel() {
        if (status != BorrowStatus.PENDING &&
                status != BorrowStatus.APPROVED) {
            throw new IllegalStateException(
                    "This request cannot be cancelled"
            );
        }

        if (borrowedAt != null) {
            throw new IllegalStateException(
                    "An asset already handed over cannot be cancelled"
            );
        }

        status = BorrowStatus.CANCELLED;
    }

    public void handOver() {
        if (status != BorrowStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only approved requests can be handed over"
            );
        }

        borrowedAt = LocalDateTime.now();
    }

    public void returnAsset(String notes) {
        if (status != BorrowStatus.APPROVED || borrowedAt == null) {
            throw new IllegalStateException(
                    "This asset has not been handed over"
            );
        }

        status = BorrowStatus.RETURNED;
        returnedAt = LocalDateTime.now();

        if (notes != null && !notes.isBlank()) {
            this.notes = notes;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}