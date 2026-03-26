package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.entity.Feedback;
import com.campus.lostfound.service.FeedbackService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class ApiFeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ApiResult<Feedback> submit(@RequestBody SubmitBody body) {
        return ApiResult.ok(feedbackService.submit(body.getContent(), body.getContact()));
    }

    @GetMapping("/mine")
    public ApiResult<Page<Feedback>> mine(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(feedbackService.mine(page, size));
    }

    @Data
    public static class SubmitBody {
        private String content;
        private String contact;
    }
}
