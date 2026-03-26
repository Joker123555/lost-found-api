package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.service.MatchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class ApiMatchController {

    private final MatchQueryService matchQueryService;

    @GetMapping
    public ApiResult<List<Map<String, Object>>> list() {
        return ApiResult.ok(matchQueryService.myMatches());
    }

    @GetMapping("/meta")
    public ApiResult<Map<String, Object>> meta() {
        return ApiResult.ok(matchQueryService.meta());
    }

    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> detail(@PathVariable long id) {
        return ApiResult.ok(matchQueryService.matchDetail(id));
    }
}
