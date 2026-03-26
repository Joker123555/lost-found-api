package com.campus.lostfound.service;

import com.campus.lostfound.entity.Item;
import com.campus.lostfound.entity.ItemImage;
import com.campus.lostfound.entity.ItemMatch;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.*;
import com.campus.lostfound.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchQueryService {

    private final ItemMatchRepository itemMatchRepository;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final UserRepository userRepository;

    public List<Map<String, Object>> myMatches() {
        Long uid = UserContext.getUserId();
        List<Item> mine = itemRepository.findByUserFiltered(uid, null, null,
                org.springframework.data.domain.PageRequest.of(0, 500)).getContent();
        Set<Long> ids = mine.stream().map(Item::getId).collect(Collectors.toSet());
        if (ids.isEmpty()) return List.of();
        List<ItemMatch> all = itemMatchRepository.findByItemIds(new ArrayList<>(ids));
        Set<Long> seen = new HashSet<>();
        List<Map<String, Object>> out = new ArrayList<>();
        for (ItemMatch m : all) {
            if (!seen.add(m.getId())) continue;
            Item other = null;
            Item self = null;
            if (ids.contains(m.getLostItemId())) {
                self = itemRepository.findById(m.getLostItemId()).orElse(null);
                other = itemRepository.findById(m.getFoundItemId()).orElse(null);
            } else if (ids.contains(m.getFoundItemId())) {
                self = itemRepository.findById(m.getFoundItemId()).orElse(null);
                other = itemRepository.findById(m.getLostItemId()).orElse(null);
            }
            if (self == null || other == null) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("matchId", m.getId());
            row.put("score", m.getScore());
            row.put("selfItem", toBrief(self));
            row.put("otherItem", toBrief(other));
            row.put("otherUser", userBrief(other.getUserId()));
            out.add(row);
        }
        out.sort((a, b) -> {
            var sa = (java.math.BigDecimal) a.get("score");
            var sb = (java.math.BigDecimal) b.get("score");
            return sb.compareTo(sa);
        });
        return out;
    }

    public Map<String, Object> matchDetail(long matchId) {
        Long uid = UserContext.getUserId();
        ItemMatch m = itemMatchRepository.findById(matchId).orElseThrow();
        Item lost = itemRepository.findById(m.getLostItemId()).orElseThrow();
        Item found = itemRepository.findById(m.getFoundItemId()).orElseThrow();
        if (!lost.getUserId().equals(uid) && !found.getUserId().equals(uid)) {
            throw new com.campus.lostfound.exception.BusinessException("无权限");
        }
        Item self = lost.getUserId().equals(uid) ? lost : found;
        Item other = lost.getUserId().equals(uid) ? found : lost;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("score", m.getScore());
        map.put("selfItem", detail(self));
        map.put("otherItem", detail(other));
        map.put("otherUser", userBrief(other.getUserId()));
        return map;
    }

    private Map<String, Object> toBrief(Item it) {
        List<ItemImage> imgs = itemImageRepository.findByItemIdOrderBySortOrderAsc(it.getId());
        String thumb = imgs.isEmpty() ? null : imgs.get(0).getImageUrl();
        return Map.of(
                "id", it.getId(),
                "title", it.getTitle(),
                "type", it.getType(),
                "location", it.getLocation(),
                "happenedAt", it.getHappenedAt(),
                "thumb", thumb == null ? "" : thumb
        );
    }

    private Map<String, Object> detail(Item it) {
        Map<String, Object> m = new LinkedHashMap<>(toBrief(it));
        m.put("description", it.getDescription());
        m.put("images", itemImageRepository.findByItemIdOrderBySortOrderAsc(it.getId()).stream()
                .map(ItemImage::getImageUrl).toList());
        return m;
    }

    private Map<String, Object> userBrief(long userId) {
        User u = userRepository.findById(userId).orElseThrow();
        return Map.of("id", u.getId(), "nickname", u.getNickname(), "avatarUrl", u.getAvatarUrl() == null ? "" : u.getAvatarUrl());
    }
}
