package com.university.club.inventory.repository;

import com.university.club.inventory.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssetCategoryRepository
        extends JpaRepository<AssetCategory, UUID> {

    Optional<AssetCategory> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}