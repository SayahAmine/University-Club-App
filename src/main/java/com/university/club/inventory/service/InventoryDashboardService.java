package com.university.club.inventory.service;

import com.university.club.inventory.dto.InventoryDashboardResponse;
import com.university.club.inventory.entity.*;
import com.university.club.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryDashboardService {

    private final AssetRepository assetRepository;
    private final BorrowRequestRepository borrowRepository;
    private final AssetMaintenanceRepository maintenanceRepository;

    public InventoryDashboardResponse getDashboard() {

        long total = assetRepository.count();

        long available = assetRepository.countByStatus(
                AssetStatus.AVAILABLE
        );

        long borrowed = assetRepository.countByStatus(
                AssetStatus.BORROWED
        );

        long maintenance = assetRepository.countByStatus(
                AssetStatus.MAINTENANCE
        );

        long retired = assetRepository.countByStatus(
                AssetStatus.RETIRED
        );

        long pending = borrowRepository.countByStatus(
                BorrowStatus.PENDING
        );

        long activeBorrowings = borrowRepository.countByStatus(
                BorrowStatus.APPROVED
        );

        long overdue = borrowRepository.countByStatus(
                BorrowStatus.OVERDUE
        );

        long ongoingMaintenance = maintenanceRepository.countByStatus(
                MaintenanceStatus.IN_PROGRESS
        );

        BigDecimal purchaseValue = assetRepository.sumPurchaseValue();

        return new InventoryDashboardResponse(
                total,
                available,
                borrowed,
                maintenance,
                retired,
                pending,
                activeBorrowings,
                overdue,
                ongoingMaintenance,
                purchaseValue == null ? BigDecimal.ZERO : purchaseValue
        );
    }
}