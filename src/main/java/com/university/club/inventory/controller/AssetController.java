package com.university.club.inventory.controller;

import com.university.club.common.security.UserPrincipal;
import com.university.club.inventory.dto.*;
import com.university.club.inventory.entity.AssetStatus;
import com.university.club.inventory.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_CREATE')")
    public AssetResponse create(
            @Valid @RequestBody CreateAssetRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return assetService.create(request, principal.getId());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    public List<AssetResponse> getAll() {
        return assetService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    public AssetResponse get(@PathVariable UUID id) {
        return assetService.get(id);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    public List<AssetResponse> getByStatus(
            @PathVariable AssetStatus status
    ) {
        return assetService.getByStatus(status);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    public AssetResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAssetRequest request
    ) {
        return assetService.update(id, request);
    }

    @PatchMapping("/{id}/retire")
    @PreAuthorize("hasAuthority('INVENTORY_DELETE')")
    public void retire(@PathVariable UUID id) {
        assetService.retire(id);
    }
}