package com.campus.lostfound.service;

import com.campus.lostfound.dto.ChatMessageRequest;
import com.campus.lostfound.entity.ChatMessage;
import com.campus.lostfound.entity.ChatSession;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.exception.BusinessException;
import com.campus.lostfound.repository.ChatMessageRepository;
import com.campus.lostfound.repository.ChatSessionRepository;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public List<Map<String, Object>> sessionList() {
        Long me = UserContext.getUserId();
        List<ChatSession> sessions = chatSessionRepository.findSessionsForUser(me);
        List<Map<String, Object>> out = new ArrayList<>();
        for (ChatSession s : sessions) {
            long other = s.getParticipantA().equals(me) ? s.getParticipantB() : s.getParticipantA();
            User u = userRepository.findById(other).orElse(null);
            long unread = chatMessageRepository.countBySessionIdAndToUserIdAndIsRead(s.getId(), me, 0);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("sessionId", s.getId());
            row.put("otherUserId", other);
            row.put("nickname", u != null ? u.getNickname() : "");
            row.put("avatarUrl", u != null && u.getAvatarUrl() != null ? u.getAvatarUrl() : "");
            row.put("lastMessage", s.getLastMessage());
            row.put("lastTime", s.getLastTime());
            row.put("unread", unread);
            out.add(row);
        }
        return out;
    }

    @Transactional
    public long openOrGetSession(long otherUserId) {
        long me = UserContext.getUserId();
        if (me == otherUserId) throw new BusinessException("不能与自己聊天");
        long a = Math.min(me, otherUserId);
        long b = Math.max(me, otherUserId);
        return chatSessionRepository.findByParticipantAAndParticipantB(a, b)
                .map(ChatSession::getId)
                .orElseGet(() -> {
                    ChatSession s = ChatSession.builder()
                            .participantA(a)
                            .participantB(b)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return chatSessionRepository.save(s).getId();
                });
    }

    public Page<ChatMessage> messages(long sessionId, int page, int size) {
        long me = UserContext.getUserId();
        ChatSession s = chatSessionRepository.findById(sessionId).orElseThrow(() -> new BusinessException("会话不存在"));
        if (!s.getParticipantA().equals(me) && !s.getParticipantB().equals(me)) {
            throw new BusinessException("无权限");
        }
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId, PageRequest.of(page, size));
    }

    @Transactional
    public ChatMessage send(long sessionId, ChatMessageRequest req) {
        long me = UserContext.getUserId();
        ChatSession s = chatSessionRepository.findById(sessionId).orElseThrow(() -> new BusinessException("会话不存在"));
        long to = s.getParticipantA().equals(me) ? s.getParticipantB() : s.getParticipantA();
        int msgType = req.getType() == null ? 0 : req.getType();
        ChatMessage m = ChatMessage.builder()
                .sessionId(sessionId)
                .fromUserId(me)
                .toUserId(to)
                .content(req.getContent())
                .type(msgType)
                .isRead(0)
                .build();
        m = chatMessageRepository.save(m);
        s.setLastMessage(msgType == 1 ? "[图片]" : req.getContent());
        s.setLastTime(LocalDateTime.now());
        chatSessionRepository.save(s);
        return m;
    }

    @Transactional
    public void markRead(long sessionId) {
        long me = UserContext.getUserId();
        List<ChatMessage> list = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId, PageRequest.of(0, 500)).getContent();
        for (ChatMessage m : list) {
            if (m.getToUserId().equals(me) && m.getIsRead() == 0) {
                m.setIsRead(1);
                chatMessageRepository.save(m);
            }
        }
    }
}
