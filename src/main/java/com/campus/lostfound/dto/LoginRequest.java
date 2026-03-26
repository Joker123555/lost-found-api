package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "账号必填")
    private String account;
    @NotBlank(message = "密码必填")
    private String password;
}
