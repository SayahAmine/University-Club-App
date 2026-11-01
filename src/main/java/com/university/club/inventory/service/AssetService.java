package com.university.club.inventory.service;

import com.university.club.identity.entity.User;
import com.university.club.identity.repository.UserRepository;
import com.university.club.inventory.dto.*;
import com.university.club.inventory.entity.*;
import com.university.club.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public AssetResponse create(
            CreateAssetRequest request,
            UUID creatorId
    ) {
        if (assetRepository.existsByInventoryCode(request.inventoryCode())) {
            throw new IllegalArgumentException(
                    "Inventory code already exists"
            );
        }

        if (request.serialNumber() != null &&
                !request.serialNumber().isBlank() &&
                assetRepository.existsBySerialNumber(request.serialNumber())) {
            throw new IllegalArgumentException(
                    "Serial number already exists"
            );
        }

        AssetCategory category = categoryRepository.findById(
                request.categoryId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Category not found")
        );

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        Asset asset = Asset.create(
                request.name(),
                request.description(),
                normalize(request.serialNumber()),
                request.inventoryCode(),
                category,
                request.purchaseDate(),
                request.purchasePrice(),
                request.location(),
                creator
        );

        try {
            return map(assetRepository.saveAndFlush(asset));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "Inventory code or serial number already exists"
            );
        }
    }

    public AssetResponse update(
            UUID assetId,
            UpdateAssetRequest request
    ) {
        Asset asset = findAsset(assetId);

        String serialNumber = normalize(request.serialNumber());

        if (serialNumber != null) {
            boolean duplicate = assetRepository.existsBySerialNumber(
                    serialNumber
            );

            if (duplicate && !serialNumber.equals(asset.getSerialNumber())) {
                throw new IllegalArgumentException(
                        "Serial number already exists"
                );
            }
        }

        AssetCategory category = categoryRepository.findById(
                request.categoryId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Category not found")
        );

        asset.update(
                request.name(),
                request.description(),
                serialNumber,
                category,
                request.condition(),
                request.purchaseDate(),
                request.purchasePrice(),
                request.location()
        );

        return map(asset);
    }

    @Transactional(readOnly = true)
    public AssetResponse get(UUID id) {
        return map(assetRepository.findWithDetailsById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Asset not found")
                ));
    }

    @Transactional(readOnly = true)
    public List<AssetResponse> getAll() {
        return assetRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssetResponse> getByStatus(AssetStatus status) {
        return assetRepository.findAllByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::map)
                .toList();
    }

    public void retire(UUID id) {
        Asset asset = assetRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Asset not found")
                );

        asset.retire();
    }

    private Asset findAsset(UUID id) {
        return assetRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Asset not found")
                );
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private AssetResponse map(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getName(),
                asset.getDescription(),
                asset.getSerialNumber(),
                asset.getInventoryCode(),
                asset.getCategory().getId(),
                asset.getCategory().getName(),
                asset.getCondition(),
                asset.getStatus(),
                asset.getPurchaseDate(),
                asset.getPurchasePrice(),
                asset.getLocation(),
                asset.getCreatedBy().getId(),
                asset.getCreatedAt(),
                asset.getUpdatedAt()
        );
    }
}