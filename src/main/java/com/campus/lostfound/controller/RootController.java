package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 避免浏览器直接访问 http://localhost:8080/ 时走静态资源解析并出现 NoResourceFoundException。
 */
@RestController
public class RootController {

    @GetMapping("/")
    public ApiResult<Map<String, String>> root() {
        return ApiResult.ok(Map.of(
                "service", "campus-lost-found-api",
                "hint", "小程序/管理端请请求 /api/* 与 /admin/*"
        ));
    }
}
