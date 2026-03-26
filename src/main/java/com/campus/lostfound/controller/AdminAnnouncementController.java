package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.entity.Announcement;
import com.campus.lostfound.service.AdminGuard;
import com.campus.lostfound.service.AnnouncementService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/announcements")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AdminGuard adminGuard;
    private final AnnouncementService announcementService;

    @GetMapping
    public ApiResult<Page<Announcement>> list(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        adminGuard.requireAdmin();
        return ApiResult.ok(announcementService.list(page, size));
    }

    @PostMapping
    public ApiResult<Announcement> save(@RequestBody AnnBody body) {
        adminGuard.requireAdmin();
        return ApiResult.ok(announcementService.save(body.getTitle(), body.getContent(), body.getId()));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable long id) {
        adminGuard.requireAdmin();
        announcementService.delete(id);
        return ApiResult.ok();
    }

    @Data
    public static class AnnBody {
        private Long id;
        private String title;
        private String content;
    }
}
