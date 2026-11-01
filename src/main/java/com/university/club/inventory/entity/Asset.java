package com.university.club.inventory.entity;

import com.university.club.identity.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assets")
@Getter
@NoArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "serial_number", unique = true, length = 150)
    private String serialNumber;

    @Column(name = "inventory_code", nullable = false, unique = true)
    private String inventoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private AssetCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetCondition condition = AssetCondition.GOOD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status = AssetStatus.AVAILABLE;

    private LocalDate purchaseDate;

    private BigDecimal purchasePrice;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Asset create(
            String name,
            String description,
            String serialNumber,
            String inventoryCode,
            AssetCategory category,
            LocalDate purchaseDate,
            BigDecimal purchasePrice,
            String location,
            User createdBy
    ) {
        Asset asset = new Asset();

        asset.name = name;
        asset.description = description;
        asset.serialNumber = serialNumber;
        asset.inventoryCode = inventoryCode;
        asset.category = category;
        asset.purchaseDate = purchaseDate;
        asset.purchasePrice = purchasePrice;
        asset.location = location;
        asset.createdBy = createdBy;

        return asset;
    }

    public void update(
            String name,
            String description,
            String serialNumber,
            AssetCategory category,
            AssetCondition condition,
            LocalDate purchaseDate,
            BigDecimal purchasePrice,
            String location
    ) {
        this.name = name;
        this.description = description;
        this.serialNumber = serialNumber;
        this.category = category;
        this.condition = condition;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.location = location;
    }

    public void markBorrowed() {
        if (status != AssetStatus.AVAILABLE) {
            throw new IllegalStateException("Asset is not available");
        }

        status = AssetStatus.BORROWED;
    }

    public void markAvailable() {
        if (status != AssetStatus.BORROWED) {
            throw new IllegalStateException("Asset is not borrowed");
        }

        status = AssetStatus.AVAILABLE;
    }

    public void startMaintenance() {
        if (status == AssetStatus.BORROWED ||
                status == AssetStatus.RETIRED) {
            throw new IllegalStateException(
                    "Asset cannot enter maintenance"
            );
        }

        status = AssetStatus.MAINTENANCE;
    }

    public void retire() {
        if (status == AssetStatus.BORROWED) {
            throw new IllegalStateException(
                    "Return the asset before retiring it"
            );
        }

        status = AssetStatus.RETIRED;
    }

    public void finishMaintenance() {
        if (status != AssetStatus.MAINTENANCE) {
            throw new IllegalStateException(
                    "Asset is not under maintenance"
            );
        }

        status = AssetStatus.AVAILABLE;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}