package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WxLoginRequest {
    @NotBlank
    private String code;

    /** 手机号动态令牌（新接口），由 button open-type="getPhoneNumber" 回调中的 e.detail.code 传入 */
    private String phoneCode;
    /** 手机号加密数据（旧接口兼容） */
    private String encryptedData;
    /** 手机号解密向量（旧接口兼容） */
    private String iv;

    private String nickname;
    private String avatarUrl;
}
