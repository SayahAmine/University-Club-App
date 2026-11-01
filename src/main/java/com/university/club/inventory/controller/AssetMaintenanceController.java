package com.university.club.inventory.controller;

import com.university.club.common.security.UserPrincipal;
import com.university.club.inventory.dto.CreateMaintenanceRequest;
import com.university.club.inventory.dto.MaintenanceResponse;
import com.university.club.inventory.service.AssetMaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maintenance")
@RequiredArgsConstructor
public class AssetMaintenanceController {

    private final AssetMaintenanceService maintenanceService;

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    public MaintenanceResponse create(
            @Valid @RequestBody CreateMaintenanceRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return maintenanceService.create(request, principal.getId());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    public List<MaintenanceResponse> getAll() {
        return maintenanceService.getAll();
    }

    @GetMapping("/assets/{assetId}")
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    public List<MaintenanceResponse> getByAsset(
            @PathVariable UUID assetId
    ) {
        return maintenanceService.getByAsset(assetId);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    public MaintenanceResponse complete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return maintenanceService.complete(id, principal.getId());
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    public MaintenanceResponse cancel(@PathVariable UUID id) {
        return maintenanceService.cancel(id);
    }
}