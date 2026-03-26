package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.dto.*;
import com.campus.lostfound.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final AuthService authService;

    @PostMapping("/send-code")
    public ApiResult<Void> sendCode(@Valid @RequestBody SendCodeRequest req) {
        authService.sendCode(req);
        return ApiResult.ok();
    }

    @PostMapping("/register")
    public ApiResult<TokenResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResult.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ApiResult<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResult.ok(authService.login(req));
    }

    @PostMapping("/wx-login")
    public ApiResult<TokenResponse> wxLogin(@Valid @RequestBody WxLoginRequest req) {
        return ApiResult.ok(authService.wxLogin(req));
    }

    @PostMapping("/reset-password")
    public ApiResult<Void> reset(@Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ApiResult.ok();
    }
}
