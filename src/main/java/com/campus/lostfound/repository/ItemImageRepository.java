package com.campus.lostfound.repository;

import com.campus.lostfound.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    List<ItemImage> findByItemIdOrderBySortOrderAsc(Long itemId);

    List<ItemImage> findByItemIdInOrderByItemIdAscSortOrderAsc(Collection<Long> itemIds);

    void deleteByItemId(Long itemId);
}
