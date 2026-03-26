package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.entity.Announcement;
import com.campus.lostfound.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class ApiAnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ApiResult<Page<Announcement>> list(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(announcementService.list(page, size));
    }

    @GetMapping("/{id}")
    public ApiResult<Announcement> get(@PathVariable long id) {
        return ApiResult.ok(announcementService.get(id));
    }
}
