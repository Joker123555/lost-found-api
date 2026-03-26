package com.campus.lostfound.service;

import com.campus.lostfound.entity.Announcement;
import com.campus.lostfound.exception.BusinessException;
import com.campus.lostfound.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public Page<Announcement> list(int page, int size) {
        return announcementRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    public Announcement get(long id) {
        return announcementRepository.findById(id).orElseThrow(() -> new BusinessException("公告不存在"));
    }

    @Transactional
    public Announcement save(String title, String content, Long id) {
        if (id == null) {
            Announcement a = Announcement.builder().title(title).content(content).build();
            return announcementRepository.save(a);
        }
        Announcement a = announcementRepository.findById(id).orElseThrow(() -> new BusinessException("公告不存在"));
        a.setTitle(title);
        a.setContent(content);
        return announcementRepository.save(a);
    }

    @Transactional
    public void delete(long id) {
        announcementRepository.deleteById(id);
    }
}
