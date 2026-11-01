package com.university.club.inventory.dto;

import java.math.BigDecimal;

public record InventoryDashboardResponse(
        long totalAssets,
        long availableAssets,
        long borrowedAssets,
        long maintenanceAssets,
        long retiredAssets,
        long pendingBorrowRequests,
        long activeBorrowings,
        long overdueBorrowings,
        long ongoingMaintenance,
        BigDecimal totalPurchaseValue
) {}