package com.university.club.membership.controller;

import com.university.club.membership.dto.CreateRoleRequest;
import com.university.club.membership.dto.RoleResponse;
import com.university.club.membership.dto.UpdateRolePermissionsRequest;
import com.university.club.membership.service.RoleManagementService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleManagementController {

    private final RoleManagementService roleManagementService;

    public RoleManagementController(
            RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    // List roles
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {

        return ResponseEntity.ok(
                roleManagementService.getAllRoles()
        );
    }

    // View role details
    @GetMapping("/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<RoleResponse> getRole(
            @PathVariable UUID roleId) {

        return ResponseEntity.ok(
                roleManagementService.getRole(roleId)
        );
    }

    // Create custom role
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<RoleResponse> createRole(
            @Valid @RequestBody CreateRoleRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roleManagementService.createRole(request));
    }

    // Update permissions of a custom role
    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<RoleResponse> updateRolePermissions(
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateRolePermissionsRequest request) {

        return ResponseEntity.ok(
                roleManagementService.updateRolePermissions(
                        roleId,
                        request.permissionCodes()
                )
        );
    }

    // Assign role to a member
    @PostMapping("/memberships/{membershipId}/assign")
    @PreAuthorize("hasAuthority('MEMBER_ROLE_ASSIGN')")
    public ResponseEntity<Void> assignRole(
            @PathVariable UUID membershipId,
            @Valid @RequestBody com.university.club.membership.dto.AssignRoleRequest request) {

        roleManagementService.assignRole(
                membershipId,
                request.roleId()
        );

        return ResponseEntity.noContent().build();
    }

    // Remove role from a member
    @DeleteMapping("/memberships/{membershipId}/{roleId}")
    @PreAuthorize("hasAuthority('MEMBER_ROLE_ASSIGN')")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID membershipId,
            @PathVariable UUID roleId) {

        roleManagementService.removeRole(membershipId, roleId);

        return ResponseEntity.noContent().build();
    }
}