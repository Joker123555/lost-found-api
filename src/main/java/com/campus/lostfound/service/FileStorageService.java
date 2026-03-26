package com.campus.lostfound.service;

import com.campus.lostfound.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final AppProperties appProperties;

    public String store(MultipartFile file) throws IOException {
        String dir = appProperties.getUploadDir();
        Path base = Paths.get(dir);
        Files.createDirectories(base);
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String name = UUID.randomUUID() + ext;
        Path target = base.resolve(name);
        file.transferTo(target.toFile());
        String baseUrl = appProperties.getPublicBaseUrl();
        return baseUrl + "/uploads/" + name;
    }
}
