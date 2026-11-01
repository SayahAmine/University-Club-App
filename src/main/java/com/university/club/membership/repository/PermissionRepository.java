package com.university.club.membership.repository;

import com.university.club.membership.entity.Permission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PermissionRepository
        extends JpaRepository<Permission, UUID> {

    List<Permission> findAllByCodeIn(Iterable<String> codes);
}