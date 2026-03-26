package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendCodeRequest {
    @NotBlank
    @Size(min = 4, max = 32, message = "账号长度4-32位")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "账号仅含字母、数字、下划线或中文")
    private String account;

    /** 目前仅支持 reset（找回密码） */
    @NotBlank
    private String type;
}
