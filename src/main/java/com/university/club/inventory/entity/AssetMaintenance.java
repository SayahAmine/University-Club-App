package com.university.club.inventory.entity;

import com.university.club.identity.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "asset_maintenance")
@Getter
@NoArgsConstructor
public class AssetMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false)
    private MaintenanceType maintenanceType;

    private BigDecimal cost;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status = MaintenanceStatus.IN_PROGRESS;

    public static AssetMaintenance create(
            Asset asset,
            String title,
            String description,
            MaintenanceType type,
            BigDecimal cost,
            User recordedBy
    ) {
        AssetMaintenance maintenance = new AssetMaintenance();

        maintenance.asset = asset;
        maintenance.title = title;
        maintenance.description = description;
        maintenance.maintenanceType = type;
        maintenance.cost = cost;
        maintenance.recordedBy = recordedBy;

        return maintenance;
    }

    public void complete(User performedBy) {
        if (status != MaintenanceStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Maintenance is not in progress"
            );
        }

        this.status = MaintenanceStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.performedBy = performedBy;
    }

    public void cancel() {
        if (status != MaintenanceStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only ongoing maintenance can be cancelled"
            );
        }

        this.status = MaintenanceStatus.CANCELLED;
    }

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }
}