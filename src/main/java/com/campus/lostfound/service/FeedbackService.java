package com.campus.lostfound.service;

import com.campus.lostfound.entity.Feedback;
import com.campus.lostfound.repository.FeedbackRepository;
import com.campus.lostfound.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Transactional
    public Feedback submit(String content, String contact) {
        Long uid = UserContext.getUserId();
        Feedback f = Feedback.builder()
                .userId(uid)
                .content(content)
                .contact(contact)
                .status(0)
                .build();
        return feedbackRepository.save(f);
    }

    public Page<Feedback> mine(int page, int size) {
        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(UserContext.getUserId(), PageRequest.of(page, size));
    }

    public Page<Feedback> adminList(int page, int size) {
        return feedbackRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    @Transactional
    public void markDone(long id) {
        Feedback f = feedbackRepository.findById(id).orElseThrow();
        f.setStatus(1);
        feedbackRepository.save(f);
    }
}
