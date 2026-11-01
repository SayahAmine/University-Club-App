package com.university.club.inventory.controller;

import com.university.club.inventory.dto.InventoryDashboardResponse;
import com.university.club.inventory.service.InventoryDashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryDashboardController {

    private final InventoryDashboardService dashboardService;


    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    public InventoryDashboardResponse dashboard() {
        return dashboardService.getDashboard();
    }


}