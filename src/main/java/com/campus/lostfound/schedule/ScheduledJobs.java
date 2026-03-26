package com.campus.lostfound.schedule;

import com.campus.lostfound.common.ItemStatus;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.repository.ClaimRepository;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.repository.SystemConfigRepository;
import com.campus.lostfound.service.MatchComputeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledJobs {

    private final ItemRepository itemRepository;
    private final ClaimRepository claimRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final MatchComputeService matchComputeService;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void recomputeMatches() {
        matchComputeService.recomputeAll();
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void expireOldItems() {
        int days = Integer.parseInt(systemConfigRepository.findById("item.expire.days")
                .map(c -> c.getConfigValue()).orElse("30"));
        LocalDateTime before = LocalDateTime.now().minusDays(days);
        List<Item> list = itemRepository.findPublishedBefore(before);
        for (Item it : list) {
            if (it.getStatus() == ItemStatus.PUBLISHED) {
                it.setStatus(ItemStatus.OFFLINE);
                itemRepository.save(it);
            }
        }
    }

    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void autoRejectClaims() {
        int days = Integer.parseInt(systemConfigRepository.findById("claim.auto.reject.days")
                .map(c -> c.getConfigValue()).orElse("7"));
        LocalDateTime before = LocalDateTime.now().minusDays(days);
        List<Claim> claims = claimRepository.findPendingClaimsOlderThan(before);
        for (Claim c : claims) {
            c.setStatus(2);
            claimRepository.save(c);
        }
    }
}
