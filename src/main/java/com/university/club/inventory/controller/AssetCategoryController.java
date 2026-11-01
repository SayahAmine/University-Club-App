package com.university.club.inventory.controller;

import com.university.club.inventory.dto.CreateAssetCategoryRequest;
import com.university.club.inventory.entity.AssetCategory;
import com.university.club.inventory.service.AssetCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/asset-categories")
@RequiredArgsConstructor
public class AssetCategoryController {

    private final AssetCategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_CREATE')")
    public AssetCategory create(
            @Valid @RequestBody CreateAssetCategoryRequest request
    ) {
        return categoryService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    public List<AssetCategory> getAll() {
        return categoryService.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_DELETE')")
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }
}