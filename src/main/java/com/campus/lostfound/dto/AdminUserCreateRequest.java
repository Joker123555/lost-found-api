package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUserCreateRequest {

    @NotBlank(message = "手机号必填")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码必填")
    @Size(min = 8, max = 20, message = "密码长度8-20位")
    private String password;

    @NotBlank(message = "昵称必填")
    @Size(max = 32)
    private String nickname;

    /** 0 普通用户 1 管理员 */
    private Integer role;

    /** 0 正常 1 封禁 */
    private Integer status;
}
