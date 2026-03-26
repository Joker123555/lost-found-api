package com.campus.lostfound.service;

import com.campus.lostfound.common.ItemStatus;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Map<String, Object> overview() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("userCount", userRepository.count());
        m.put("itemPublished", itemRepository.countByIsDeletedAndStatus(0, ItemStatus.PUBLISHED));
        m.put("itemPending", itemRepository.countByIsDeletedAndStatus(0, ItemStatus.PENDING));
        return m;
    }

    public Map<String, Object> trend() {
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            labels.add(d.toString());
            values.add(0L);
        }
        return Map.of("labels", labels, "values", values);
    }
}
