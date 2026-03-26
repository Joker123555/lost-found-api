package com.campus.lostfound.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private String uploadDir;
    private String publicBaseUrl;
    private Wechat wechat = new Wechat();
    private Cors cors = new Cors();
    /** 发布策略：requireAudit=true 时新建/编辑后为待审核，仅审核通过后出现在首页公开列表 */
    private Items items = new Items();

    @Data
    public static class Jwt {
        private String userSecret;
        private String adminSecret;
        private int userExpireDays = 7;
        private int adminExpireHours = 24;
    }

    @Data
    public static class Wechat {
        private String appId;
        private String appSecret;
    }

    @Data
    public static class Cors {
        private List<String> allowedOrigins;
    }

    @Data
    public static class Items {
        /** 默认 true：与业务「提交审核」流程一致；本地可改为 false 跳过审核 */
        private boolean requireAudit = true;
    }
}
