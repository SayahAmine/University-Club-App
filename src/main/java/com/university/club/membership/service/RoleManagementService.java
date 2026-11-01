package com.university.club.membership.service;

import com.university.club.common.exception.ResourceNotFoundException;
import com.university.club.membership.dto.CreateRoleRequest;
import com.university.club.membership.dto.RoleResponse;
import com.university.club.membership.entity.Membership;
import com.university.club.membership.entity.Role;
import com.university.club.membership.entity.Permission;
import com.university.club.membership.exception.ProtectedRoleException;
import com.university.club.membership.repository.MembershipRepository;
import com.university.club.membership.repository.PermissionRepository;
import com.university.club.membership.repository.RoleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/*
Design decision: Only custom roles can have their permissions edited through
 this API. The predefined system roles retain their controlled
  permission definitions. This prevents an administrator from accidentally removing critical permissions from a system role.
 */

@Service
public class RoleManagementService {

    private static final String PRESIDENT = "PRESIDENT";

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MembershipRepository membershipRepository;

    public RoleManagementService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            MembershipRepository membershipRepository) {

        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.membershipRepository = membershipRepository;
    }

    // List all roles and their permissions
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {

        return roleRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // View one role
    @Transactional(readOnly = true)
    public RoleResponse getRole(UUID roleId) {

        Role role = findRoleWithPermissions(roleId);

        return toResponse(role);
    }

    // Create a custom role
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {

        String name = request.name().trim().toUpperCase();

        if (roleRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "A role with this name already exists.");
        }

        Role role = Role.createCustom(
                name,
                request.description()
        );

        return toResponse(roleRepository.save(role));
    }

    // Update permissions of a custom role
    @Transactional
    public RoleResponse updateRolePermissions(
            UUID roleId,
            Set<String> permissionCodes) {

        Role role = findRoleWithPermissions(roleId);

        if (role.isSystem()) {
            throw new ProtectedRoleException(
                    "System role permissions cannot be modified.");
        }

        Set<String> normalizedCodes = permissionCodes.stream()
                .map(code -> code.trim().toUpperCase())
                .collect(Collectors.toSet());

        List<Permission> permissions =
                permissionRepository.findAllByCodeIn(normalizedCodes);

        if (permissions.size() != normalizedCodes.size()) {
            Set<String> foundCodes = permissions.stream()
                    .map(Permission::getCode)
                    .collect(Collectors.toSet());

            Set<String> unknownCodes = normalizedCodes.stream()
                    .filter(code -> !foundCodes.contains(code))
                    .collect(Collectors.toSet());

            throw new IllegalArgumentException(
                    "Unknown permission codes: " + unknownCodes);
        }

        role.updatePermissions(Set.copyOf(permissions));

        return toResponse(role);
    }

    // Assign a role to a membership
    @Transactional
    public void assignRole(UUID membershipId, UUID roleId) {

        Membership membership = findMembership(membershipId);
        Role role = findRoleWithPermissions(roleId);

        if (PRESIDENT.equals(role.getName())) {
            throw new ProtectedRoleException(
                    "The PRESIDENT role can only be assigned through the "
                            + "controlled bootstrap or president-transfer process.");
        }

        membership.addRole(role);
    }

    // Remove a role from a membership
    @Transactional
    public void removeRole(UUID membershipId, UUID roleId) {

        Membership membership = findMembership(membershipId);
        Role role = findRoleWithPermissions(roleId);

        if (PRESIDENT.equals(role.getName())) {
            throw new ProtectedRoleException(
                    "The PRESIDENT role cannot be removed through "
                            + "ordinary role management.");
        }

        if (!membership.getRoles().contains(role)) {
            throw new ResourceNotFoundException(
                    "This role is not assigned to the membership.");
        }

        membership.removeRole(role);
    }

    private Role findRoleWithPermissions(UUID roleId) {

        return roleRepository.findWithPermissionsById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found"));
    }

    private Membership findMembership(UUID membershipId) {

        return membershipRepository.findById(membershipId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Membership not found"));
    }

    private RoleResponse toResponse(Role role) {

        Set<String> permissionCodes = role.getPermissions()
                .stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isSystem(),
                permissionCodes
        );
    }
}