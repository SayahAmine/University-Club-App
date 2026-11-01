package com.university.club.inventory.service;

import com.university.club.identity.entity.User;
import com.university.club.identity.entity.UserStatus;
import com.university.club.identity.repository.UserRepository;
import com.university.club.inventory.dto.CreateMaintenanceRequest;
import com.university.club.inventory.dto.MaintenanceResponse;
import com.university.club.inventory.entity.*;
import com.university.club.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetMaintenanceService {

    private final AssetMaintenanceRepository maintenanceRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public MaintenanceResponse create(
            CreateMaintenanceRequest request,
            UUID recordedById
    ) {
        Asset asset = assetRepository.findByIdForUpdate(
                request.assetId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Asset not found")
        );

        if (asset.getStatus() == AssetStatus.BORROWED ||
                asset.getStatus() == AssetStatus.RETIRED) {
            throw new IllegalStateException(
                    "This asset cannot enter maintenance"
            );
        }

        User recordedBy = findUser(recordedById);

        asset.startMaintenance();

        AssetMaintenance maintenance = AssetMaintenance.create(
                asset,
                request.title(),
                request.description(),
                request.type(),
                request.cost(),
                recordedBy
        );

        return map(maintenanceRepository.save(maintenance));
    }

    public MaintenanceResponse complete(
            UUID maintenanceId,
            UUID performedById
    ) {
        AssetMaintenance maintenance = maintenanceRepository
                .findById(maintenanceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Maintenance record not found"
                        )
                );

        Asset asset = assetRepository.findByIdForUpdate(
                maintenance.getAsset().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Asset not found")
        );

        User performedBy = findUser(performedById);

        maintenance.complete(performedBy);

        asset.finishMaintenance();

        return map(maintenance);
    }

    public MaintenanceResponse cancel(UUID maintenanceId) {

        AssetMaintenance maintenance = maintenanceRepository
                .findById(maintenanceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Maintenance record not found"
                        )
                );

        Asset asset = assetRepository.findByIdForUpdate(
                maintenance.getAsset().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Asset not found")
        );

        maintenance.cancel();

        asset.finishMaintenance();

        return map(maintenance);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getAll() {
        return maintenanceRepository.findAllByOrderByStartedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getByAsset(UUID assetId) {
        return maintenanceRepository
                .findAllByAsset_IdOrderByStartedAtDesc(assetId)
                .stream()
                .map(this::map)
                .toList();
    }

    private User findUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        if (user.getAccountStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("User account is not active");
        }

        return user;
    }

    private MaintenanceResponse map(AssetMaintenance maintenance) {
        Asset asset = maintenance.getAsset();

        return new MaintenanceResponse(
                maintenance.getId(),
                asset.getId(),
                asset.getName(),
                asset.getInventoryCode(),
                maintenance.getTitle(),
                maintenance.getDescription(),
                maintenance.getMaintenanceType(),
                maintenance.getCost(),
                maintenance.getStartedAt(),
                maintenance.getCompletedAt(),
                maintenance.getPerformedBy() == null
                        ? null : maintenance.getPerformedBy().getId(),
                maintenance.getRecordedBy().getId(),
                maintenance.getStatus()
        );
    }
}