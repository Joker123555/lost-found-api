package com.campus.lostfound.repository;

import com.campus.lostfound.entity.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Page<Claim> findByClaimantIdAndIsDeleted(Long claimantId, int isDeleted, Pageable pageable);

    List<Claim> findByItemIdAndIsDeleted(Long itemId, int isDeleted);

    @Query("SELECT c FROM Claim c WHERE c.status = 0 AND c.createdAt < :before")
    List<Claim> findPendingClaimsOlderThan(@Param("before") java.time.LocalDateTime before);
}
