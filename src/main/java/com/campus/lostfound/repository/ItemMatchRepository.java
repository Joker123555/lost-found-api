package com.campus.lostfound.repository;

import com.campus.lostfound.entity.ItemMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemMatchRepository extends JpaRepository<ItemMatch, Long> {

    @Query("SELECT m FROM ItemMatch m WHERE m.lostItemId IN :ids OR m.foundItemId IN :ids")
    List<ItemMatch> findByItemIds(@Param("ids") List<Long> itemIds);

    @Query("SELECT m FROM ItemMatch m WHERE m.lostItemId = :lid OR m.foundItemId = :fid")
    List<ItemMatch> findByPair(@Param("lid") Long lostId, @Param("fid") Long foundId);
}
