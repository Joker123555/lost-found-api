package com.campus.lostfound.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步全量匹配重算，避免发布/更新接口长时间阻塞。
 */
@Service
@RequiredArgsConstructor
public class MatchAsyncService {

    private final MatchComputeService matchComputeService;

    @Async("matchTaskExecutor")
    public void recomputeAllLater() {
        matchComputeService.recomputeAll();
    }
}
