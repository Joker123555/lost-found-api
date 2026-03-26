package com.campus.lostfound.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * AC3 P1：匹配结果微信模板消息/订阅消息。
 * <p>
 * 接入小程序服务端 API 后，在 {@link #onRecomputeFinished(long)} 中按 userId 去重并发送；
 * 需配置模板 ID、用户授权记录等，当前仅占位打日志。
 */
@Service
public class MatchNotifyService {

    private static final Logger log = LoggerFactory.getLogger(MatchNotifyService.class);

    public void onRecomputeFinished(long matchRowCount) {
        log.info("[P1-模板消息] 匹配表当前 {} 条，订阅消息未接入", matchRowCount);
    }
}
