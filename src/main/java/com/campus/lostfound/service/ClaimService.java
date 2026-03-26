package com.campus.lostfound.service;

import com.campus.lostfound.common.ItemStatus;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.exception.BusinessException;
import com.campus.lostfound.repository.ClaimRepository;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Claim claim(long itemId, String message) {
        Long uid = UserContext.getUserId();
        Item it = itemRepository.findById(itemId).orElseThrow(() -> new BusinessException("物品不存在"));
        if (it.getStatus() == null || it.getStatus() != ItemStatus.PUBLISHED) {
            throw new BusinessException("抱歉，该物品已被认领或不可认领");
        }
        if (it.getUserId().equals(uid)) throw new BusinessException("不能认领自己的发布");
        Claim c = Claim.builder()
                .itemId(itemId)
                .claimantId(uid)
                .message(message)
                .status(0)
                .build();
        return claimRepository.save(c);
    }

    @Transactional
    public void agree(long claimId) {
        Long uid = UserContext.getUserId();
        Claim c = claimRepository.findById(claimId).orElseThrow(() -> new BusinessException("记录不存在"));
        Item it = itemRepository.findById(c.getItemId()).orElseThrow();
        if (!it.getUserId().equals(uid)) throw new BusinessException("无权限");
        c.setStatus(1);
        claimRepository.save(c);
        it.setStatus(ItemStatus.CLAIMED);
        itemRepository.save(it);
    }

    public Page<Claim> myClaims(int page, int size) {
        return claimRepository.findByClaimantIdAndIsDeleted(UserContext.getUserId(), 0, PageRequest.of(page, size));
    }
}
