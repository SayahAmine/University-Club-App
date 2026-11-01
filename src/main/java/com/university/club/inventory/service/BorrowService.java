package com.university.club.inventory.service;

import com.university.club.identity.entity.User;
import com.university.club.identity.repository.UserRepository;
import com.university.club.inventory.dto.*;
import com.university.club.inventory.entity.*;
import com.university.club.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BorrowService {

    private final BorrowRequestRepository borrowRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public BorrowResponse request(
            CreateBorrowRequest request,
            UUID userId
    ) {
        validateDates(request.requestedFrom(), request.requestedUntil());

        User user = findUser(userId);

        Asset asset = assetRepository.findByIdForUpdate(
                request.assetId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Asset not found")
        );

        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Asset is not available"
            );
        }

        BorrowRequest borrowRequest = BorrowRequest.create(
                asset,
                user,
                request.purpose(),
                request.requestedFrom(),
                request.requestedUntil()
        );

        return map(borrowRepository.save(borrowRequest));
    }

    public BorrowResponse approve(
            UUID requestId,
            UUID reviewerId,
            BorrowReviewRequest request
    ) {
        BorrowRequest borrowRequest = findRequestForUpdate(requestId);

        User reviewer = findUser(reviewerId);

        if (borrowRequest.getAsset().getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Asset is no longer available"
            );
        }

        if (borrowRequest.getRequestedUntil().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "The requested borrowing period has expired"
            );
        }

        borrowRequest.approve(
                reviewer,
                request == null ? null : request.notes()
        );

        return map(borrowRequest);
    }

    public BorrowResponse reject(
            UUID requestId,
            UUID reviewerId,
            BorrowReviewRequest request
    ) {
        BorrowRequest borrowRequest = findRequestForUpdate(requestId);

        User reviewer = findUser(reviewerId);

        borrowRequest.reject(
                reviewer,
                request == null ? null : request.notes()
        );

        return map(borrowRequest);
    }

    public BorrowResponse handOver(UUID requestId) {

        BorrowRequest borrowRequest = findRequestForUpdate(requestId);

        Asset asset = assetRepository.findByIdForUpdate(
                borrowRequest.getAsset().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Asset not found")
        );

        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Asset is not available"
            );
        }

        borrowRequest.handOver();
        asset.markBorrowed();

        return map(borrowRequest);
    }

    public BorrowResponse returnAsset(
            UUID requestId,
            ReturnAssetRequest request
    ) {
        BorrowRequest borrowRequest = findRequestForUpdate(requestId);

        Asset asset = assetRepository.findByIdForUpdate(
                borrowRequest.getAsset().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Asset not found")
        );

        borrowRequest.returnAsset(request.notes());

        asset.markAvailable();

        if (request.condition() != asset.getCondition()) {
            asset.update(
                    asset.getName(),
                    asset.getDescription(),
                    asset.getSerialNumber(),
                    asset.getCategory(),
                    request.condition(),
                    asset.getPurchaseDate(),
                    asset.getPurchasePrice(),
                    asset.getLocation()
            );
        }

        if (request.condition() == AssetCondition.DAMAGED ||
                request.condition() == AssetCondition.BROKEN) {
            asset.startMaintenance();
        }

        return map(borrowRequest);
    }

    public BorrowResponse cancel(
            UUID requestId,
            UUID userId
    ) {
        BorrowRequest borrowRequest = findRequestForUpdate(requestId);

        if (!borrowRequest.getUser().getId().equals(userId)) {
            throw new IllegalStateException(
                    "You cannot cancel another user's request"
            );
        }

        borrowRequest.cancel();

        return map(borrowRequest);
    }

    @Transactional(readOnly = true)
    public List<BorrowResponse> getMyRequests(UUID userId) {
        return borrowRepository.findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BorrowResponse> getAllRequests() {
        return borrowRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BorrowResponse> getPendingRequests() {
        return borrowRepository
                .findAllByStatusOrderByCreatedAtAsc(BorrowStatus.PENDING)
                .stream()
                .map(this::map)
                .toList();
    }

    private void validateDates(
            LocalDateTime from,
            LocalDateTime until
    ) {
        if (!until.isAfter(from)) {
            throw new IllegalArgumentException(
                    "The return date must be after the borrowing date"
            );
        }

        if (!from.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "The borrowing start date must be in the future"
            );
        }
    }

    private User findUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        if (user.getAccountStatus() !=
                com.university.club.identity.entity.UserStatus.ACTIVE) {
            throw new IllegalStateException(
                    "User account is not active"
            );
        }

        return user;
    }

    private BorrowRequest findRequestForUpdate(UUID id) {
        return borrowRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Borrow request not found"
                        )
                );
    }

    private BorrowResponse map(BorrowRequest request) {
        Asset asset = request.getAsset();
        User user = request.getUser();

        return new BorrowResponse(
                request.getId(),
                asset.getId(),
                asset.getName(),
                asset.getInventoryCode(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                request.getPurpose(),
                request.getRequestedFrom(),
                request.getRequestedUntil(),
                request.getStatus(),
                request.getReviewedBy() == null
                        ? null : request.getReviewedBy().getId(),
                request.getReviewedAt(),
                request.getBorrowedAt(),
                request.getReturnedAt(),
                request.getNotes(),
                request.getCreatedAt()
        );
    }
}