package com.university.club.inventory.service;

import com.university.club.inventory.dto.CreateAssetCategoryRequest;
import com.university.club.inventory.entity.AssetCategory;
import com.university.club.inventory.repository.AssetCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetCategoryService {

    private final AssetCategoryRepository repository;

    public AssetCategory create(CreateAssetCategoryRequest request) {

        if (repository.existsByNameIgnoreCase(request.name().trim())) {
            throw new IllegalArgumentException(
                    "Category already exists"
            );
        }

        AssetCategory category = new AssetCategory(
                request.name().trim(),
                request.description()
        );

        return repository.save(category);
    }

    @Transactional(readOnly = true)
    public List<AssetCategory> getAll() {
        return repository.findAll();
    }

    public void delete(UUID id) {
        AssetCategory category = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found")
                );

        repository.delete(category);
    }
}