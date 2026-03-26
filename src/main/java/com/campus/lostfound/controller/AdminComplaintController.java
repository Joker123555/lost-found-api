package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.entity.Complaint;
import com.campus.lostfound.service.AdminComplaintService;
import com.campus.lostfound.service.AdminGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/complaints")
@RequiredArgsConstructor
public class AdminComplaintController {

    private final AdminGuard adminGuard;
    private final AdminComplaintService adminComplaintService;

    @GetMapping
    public ApiResult<Page<Complaint>> list(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        adminGuard.requireAdmin();
        return ApiResult.ok(adminComplaintService.list(page, size));
    }

    @PostMapping("/{id}/done")
    public ApiResult<Void> done(@PathVariable long id) {
        adminGuard.requireAdmin();
        adminComplaintService.markDone(id);
        return ApiResult.ok();
    }
}
