package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class ApiUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping
    public ApiResult<Map<String, String>> upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ApiResult.fail("文件为空");
        }
        String url = fileStorageService.store(file);
        return ApiResult.ok(Map.of("url", url));
    }
}
