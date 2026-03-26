package com.campus.lostfound.service;

import com.campus.lostfound.common.ItemStatus;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.entity.ItemMatch;
import com.campus.lostfound.repository.ItemMatchRepository;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchComputeService {

    private final ItemRepository itemRepository;
    private final ItemMatchRepository itemMatchRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final MatchNotifyService matchNotifyService;

    @Transactional
    public void recomputeAll() {
        itemMatchRepository.deleteAll();
        List<Item> lost = itemRepository.findByIsDeletedAndStatusAndType(0, ItemStatus.PUBLISHED, 0);
        List<Item> found = itemRepository.findByIsDeletedAndStatusAndType(0, ItemStatus.PUBLISHED, 1);
        BigDecimal threshold =
                new BigDecimal(
                        systemConfigRepository
                                .findById("match.score.threshold")
                                .map(c -> c.getConfigValue())
                                .orElse("60"));
        double minScore = threshold.doubleValue();
        int saved = 0;
        for (Item l : lost) {
            for (Item f : found) {
                if (l.getUserId().equals(f.getUserId())) continue;
                double score = score(l, f);
                if (score >= minScore) {
                    itemMatchRepository.save(
                            ItemMatch.builder()
                                    .lostItemId(l.getId())
                                    .foundItemId(f.getId())
                                    .score(
                                            BigDecimal.valueOf(score)
                                                    .setScale(2, RoundingMode.HALF_UP))
                                    .isNotified(0)
                                    .build());
                    saved++;
                }
            }
        }
        matchNotifyService.onRecomputeFinished(saved);
    }

    /** 分类 25 + 标题词 35 + 地点 20 + 描述 20，封顶 100；阈值可从 system_config match.score.threshold 读取（默认 60） */
    private double score(Item lost, Item found) {
        double s = 0;
        if (Objects.equals(lost.getCategoryId(), found.getCategoryId())) s += 25;
        s += jaccard(tokenize(lost.getTitle()), tokenize(found.getTitle())) * 35;
        s += jaccard(tokenize(lost.getLocation()), tokenize(found.getLocation())) * 20;
        s += jaccard(tokenize(lost.getDescription()), tokenize(found.getDescription())) * 20;
        return Math.min(100, s);
    }

    /**
     * 空格分词 + 拉丁词；含中日韩字符的片段使用二字元组（适合无空格中文），提升标题/描述匹配效果。
     */
    private Set<String> tokenize(String s) {
        if (s == null || s.isBlank()) return Set.of();
        String norm = s.replaceAll("[\\p{Punct}\\s]+", " ").trim();
        if (norm.isBlank()) return Set.of();
        Set<String> tokens = new HashSet<>();
        for (String part : norm.split("\\s+")) {
            if (part.isBlank()) continue;
            if (part.codePoints().anyMatch(MatchComputeService::isCjkScript)) {
                addCjkBigrams(part, tokens);
            } else {
                tokens.add(part.toLowerCase(Locale.ROOT));
            }
        }
        return tokens;
    }

    private static boolean isCjkScript(int cp) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(cp);
        return sc == Character.UnicodeScript.HAN
                || sc == Character.UnicodeScript.HIRAGANA
                || sc == Character.UnicodeScript.KATAKANA
                || sc == Character.UnicodeScript.HANGUL;
    }

    private void addCjkBigrams(String s, Set<String> out) {
        int n = s.length();
        if (n <= 2) {
            out.add(s);
            return;
        }
        for (int i = 0; i < n - 1; i++) {
            out.add(s.substring(i, i + 2));
        }
    }

    private double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) return 0;
        Set<String> inter = new HashSet<>(a);
        inter.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return (double) inter.size() / union.size();
    }
}
