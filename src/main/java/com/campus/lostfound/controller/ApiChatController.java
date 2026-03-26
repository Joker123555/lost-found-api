package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.dto.ChatMessageRequest;
import com.campus.lostfound.entity.ChatMessage;
import com.campus.lostfound.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ApiChatController {

    private final ChatService chatService;

    @GetMapping("/sessions")
    public ApiResult<List<Map<String, Object>>> sessions() {
        return ApiResult.ok(chatService.sessionList());
    }

    @PostMapping("/sessions/open/{otherUserId}")
    public ApiResult<Map<String, Long>> open(@PathVariable long otherUserId) {
        long sid = chatService.openOrGetSession(otherUserId);
        return ApiResult.ok(Map.of("sessionId", sid));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ApiResult<Page<ChatMessage>> messages(@PathVariable long sessionId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "50") int size) {
        return ApiResult.ok(chatService.messages(sessionId, page, size));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ApiResult<ChatMessage> send(@PathVariable long sessionId, @Valid @RequestBody ChatMessageRequest req) {
        return ApiResult.ok(chatService.send(sessionId, req));
    }

    @PostMapping("/sessions/{sessionId}/read")
    public ApiResult<Void> read(@PathVariable long sessionId) {
        chatService.markRead(sessionId);
        return ApiResult.ok();
    }
}
