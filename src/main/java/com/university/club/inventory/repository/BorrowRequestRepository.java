package com.university.club.inventory.repository;

import com.university.club.inventory.entity.BorrowRequest;
import com.university.club.inventory.entity.BorrowStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BorrowRequestRepository
        extends JpaRepository<BorrowRequest, UUID> {

    @EntityGraph(attributePaths = {"asset", "user"})
    List<BorrowRequest> findAllByUser_IdOrderByCreatedAtDesc(
            UUID userId
    );

    @EntityGraph(attributePaths = {"asset", "user"})
    List<BorrowRequest> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"asset", "user"})
    List<BorrowRequest> findAllByStatusOrderByCreatedAtAsc(
            BorrowStatus status
    );

    @EntityGraph(attributePaths = {"asset", "user"})
    Optional<BorrowRequest> findWithDetailsById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BorrowRequest b WHERE b.id = :id")
    Optional<BorrowRequest> findByIdForUpdate(@Param("id") UUID id);

    boolean existsByAsset_IdAndStatus(
            UUID assetId,
            BorrowStatus status
    );

    long countByStatus(BorrowStatus status);

    @Query("""
    SELECT b FROM BorrowRequest b
    WHERE b.status = :status
    AND b.borrowedAt IS NOT NULL
    AND b.requestedUntil < :now
""")
    List<BorrowRequest> findOverdueRequests(
            @Param("status") BorrowStatus status,
            @Param("now") LocalDateTime now
    );
}