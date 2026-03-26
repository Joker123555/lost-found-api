package com.campus.lostfound.service;

import com.campus.lostfound.config.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * 微信小程序服务端：code 换 openid、手机号 code 换号码（需配置 app-id / app-secret）。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WechatMpService {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    private volatile String cachedAccessToken;
    private volatile long accessTokenExpireAtMs;

    private boolean isWechatConfigured() {
        String s = appProperties.getWechat().getAppSecret();
        return s != null && !s.isBlank() && !"your-secret".equals(s.trim());
    }

    /**
     * jscode2session，失败时返回 empty（调用方应中止登录并提示配置）。
     */
    public Optional<WxSession> exchangeCode2Session(String jsCode) {
        if (!isWechatConfigured()) {
            return Optional.empty();
        }
        String appId = appProperties.getWechat().getAppId();
        String secret = appProperties.getWechat().getAppSecret();
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, secret, jsCode);
        try {
            RestClient client = RestClient.create();
            String raw = client.get().uri(url).retrieve().body(String.class);
            JsonNode root = objectMapper.readTree(raw);
            if (root.hasNonNull("errcode") && root.get("errcode").asInt() != 0) {
                log.warn("jscode2session failed: {}", raw);
                return Optional.empty();
            }
            if (root.hasNonNull("openid") && root.hasNonNull("session_key")) {
                return Optional.of(new WxSession(
                        root.get("openid").asText(),
                        root.get("session_key").asText()
                ));
            }
        } catch (Exception e) {
            log.warn("jscode2session error: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<String> exchangeCodeForOpenid(String jsCode) {
        return exchangeCode2Session(jsCode).map(WxSession::openid);
    }

    /**
     * 手机号快速验证 code 换手机号；未配置或失败返回 empty。
     */
    public Optional<String> getPhoneNumberFromCode(String phoneCode) {
        if (!isWechatConfigured() || phoneCode == null || phoneCode.isBlank()) {
            return Optional.empty();
        }
        String token = getAccessToken();
        if (token == null) {
            return Optional.empty();
        }
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + token;
        try {
            RestClient client = RestClient.create();
            String bodyJson = objectMapper.createObjectNode().put("code", phoneCode).toString();
            String raw = client.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bodyJson)
                    .retrieve()
                    .body(String.class);
            JsonNode root = objectMapper.readTree(raw);
            if (root.hasNonNull("errcode") && root.get("errcode").asInt() != 0) {
                log.warn("getuserphonenumber failed: {}", raw);
                return Optional.empty();
            }
            JsonNode phoneInfo = root.path("phone_info");
            if (phoneInfo.hasNonNull("phoneNumber")) {
                return Optional.of(phoneInfo.get("phoneNumber").asText());
            }
        } catch (Exception e) {
            log.warn("getuserphonenumber error: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * 兼容旧版手机号授权：session_key + iv + encryptedData 解密手机号。
     */
    public Optional<String> decryptPhoneNumber(String sessionKey, String encryptedData, String iv) {
        if (sessionKey == null || sessionKey.isBlank()
                || encryptedData == null || encryptedData.isBlank()
                || iv == null || iv.isBlank()) {
            return Optional.empty();
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(sessionKey);
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            byte[] encBytes = Base64.getDecoder().decode(encryptedData);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] plain = cipher.doFinal(encBytes);
            String json = new String(plain, StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(json);
            if (node.hasNonNull("phoneNumber")) {
                return Optional.of(node.get("phoneNumber").asText());
            }
        } catch (Exception e) {
            log.warn("decryptPhoneNumber error: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private String getAccessToken() {
        long now = System.currentTimeMillis();
        if (cachedAccessToken != null && now < accessTokenExpireAtMs - 60_000) {
            return cachedAccessToken;
        }
        String appId = appProperties.getWechat().getAppId();
        String secret = appProperties.getWechat().getAppSecret();
        String url = String.format(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appId, secret);
        try {
            RestClient client = RestClient.create();
            String raw = client.get().uri(url).retrieve().body(String.class);
            JsonNode root = objectMapper.readTree(raw);
            if (root.hasNonNull("errcode") && root.get("errcode").asInt() != 0) {
                log.warn("getAccessToken failed: {}", raw);
                return null;
            }
            if (root.hasNonNull("access_token")) {
                cachedAccessToken = root.get("access_token").asText();
                int expires = root.has("expires_in") ? root.get("expires_in").asInt() : 7200;
                accessTokenExpireAtMs = now + expires * 1000L;
                return cachedAccessToken;
            }
        } catch (Exception e) {
            log.warn("getAccessToken error: {}", e.getMessage());
        }
        return null;
    }

    public record WxSession(String openid, String sessionKey) {
    }
}
