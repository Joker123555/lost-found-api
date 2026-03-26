package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.entity.Feedback;
import com.campus.lostfound.service.AdminGuard;
import com.campus.lostfound.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final AdminGuard adminGuard;
    private final FeedbackService feedbackService;

    @GetMapping
    public ApiResult<Page<Feedback>> list(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        adminGuard.requireAdmin();
        return ApiResult.ok(feedbackService.adminList(page, size));
    }

    @PostMapping("/{id}/done")
    public ApiResult<Void> done(@PathVariable long id) {
        adminGuard.requireAdmin();
        feedbackService.markDone(id);
        return ApiResult.ok();
    }
}
