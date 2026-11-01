package com.university.club.inventory.repository;

import com.university.club.inventory.entity.Asset;
import com.university.club.inventory.entity.AssetStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {

    boolean existsByInventoryCode(String inventoryCode);

    boolean existsBySerialNumber(String serialNumber);

    List<Asset> findAllByOrderByCreatedAtDesc();

    List<Asset> findAllByStatusOrderByCreatedAtDesc(
            AssetStatus status
    );

    @EntityGraph(attributePaths = {"category", "createdBy"})
    Optional<Asset> findWithDetailsById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE a.id = :id")
    Optional<Asset> findByIdForUpdate(@Param("id") UUID id);

    long countByStatus(AssetStatus assetStatus);

    @Query("SELECT COALESCE(SUM(a.purchasePrice), 0) FROM Asset a")
    BigDecimal sumPurchaseValue();
}