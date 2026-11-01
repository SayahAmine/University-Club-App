package com.university.club.inventory.controller;

import com.university.club.common.security.UserPrincipal;
import com.university.club.inventory.dto.*;
import com.university.club.inventory.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/borrow-requests")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    // Students submit borrowing requests.
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public BorrowResponse request(
            @Valid @RequestBody CreateBorrowRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return borrowService.request(request, principal.getId());
    }

    // Students view their own requests.
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public List<BorrowResponse> myRequests(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return borrowService.getMyRequests(principal.getId());
    }

    // Inventory managers view all requests.
    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_BORROW_APPROVE')")
    public List<BorrowResponse> allRequests() {
        return borrowService.getAllRequests();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('INVENTORY_BORROW_APPROVE')")
    public List<BorrowResponse> pendingRequests() {
        return borrowService.getPendingRequests();
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('INVENTORY_BORROW_APPROVE')")
    public BorrowResponse approve(
            @PathVariable UUID id,
            @RequestBody(required = false) BorrowReviewRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return borrowService.approve(id, principal.getId(), request);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('INVENTORY_BORROW_APPROVE')")
    public BorrowResponse reject(
            @PathVariable UUID id,
            @RequestBody(required = false) BorrowReviewRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return borrowService.reject(id, principal.getId(), request);
    }

    // Physical handover is restricted to inventory managers.
    @PatchMapping("/{id}/handover")
    @PreAuthorize("hasAuthority('INVENTORY_BORROW_APPROVE')")
    public BorrowResponse handOver(@PathVariable UUID id) {
        return borrowService.handOver(id);
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasAuthority('INVENTORY_RETURN')")
    public BorrowResponse returnAsset(
            @PathVariable UUID id,
            @Valid @RequestBody ReturnAssetRequest request
    ) {
        return borrowService.returnAsset(id, request);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public BorrowResponse cancel(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return borrowService.cancel(id, principal.getId());
    }
}