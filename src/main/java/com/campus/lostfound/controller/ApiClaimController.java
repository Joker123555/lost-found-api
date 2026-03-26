package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.service.ClaimService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ApiClaimController {

    private final ClaimService claimService;

    @PostMapping
    public ApiResult<Claim> create(@RequestBody ClaimBody body) {
        return ApiResult.ok(claimService.claim(body.getItemId(), body.getMessage()));
    }

    @PostMapping("/{id}/agree")
    public ApiResult<Void> agree(@PathVariable long id) {
        claimService.agree(id);
        return ApiResult.ok();
    }

    @GetMapping("/mine")
    public ApiResult<Page<Claim>> mine(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(claimService.myClaims(page, size));
    }

    @Data
    public static class ClaimBody {
        private long itemId;
        private String message;
    }
}
