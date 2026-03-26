package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.dto.ItemRequest;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ApiItemController {

    private final ItemService itemService;

    @GetMapping
    public ApiResult<Page<Item>> list(@RequestParam(required = false) Integer type,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(itemService.listPublished(type, keyword, categoryId, page, size));
    }

    @GetMapping("/mine")
    public ApiResult<Page<Item>> mine(@RequestParam(required = false) Integer type,
                                      @RequestParam(required = false) Integer status,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(itemService.myItems(type, status, page, size));
    }

    @GetMapping("/mine/pending-counts")
    public ApiResult<Map<String, Long>> minePendingCounts() {
        return ApiResult.ok(itemService.minePendingCounts());
    }

    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> detail(@PathVariable long id) {
        return ApiResult.ok(itemService.detailWithImages(id));
    }

    @PostMapping
    public ApiResult<Item> create(@Valid @RequestBody ItemRequest req) {
        return ApiResult.ok(itemService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResult<Item> update(@PathVariable long id, @Valid @RequestBody ItemRequest req) {
        return ApiResult.ok(itemService.update(id, req));
    }

    @PostMapping("/{id}/offline")
    public ApiResult<Void> offline(@PathVariable long id) {
        itemService.offline(id);
        return ApiResult.ok();
    }
}
