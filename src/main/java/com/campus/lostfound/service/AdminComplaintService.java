package com.campus.lostfound.service;

import com.campus.lostfound.entity.Complaint;
import com.campus.lostfound.exception.BusinessException;
import com.campus.lostfound.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminComplaintService {

    private final ComplaintRepository complaintRepository;

    public Page<Complaint> list(int page, int size) {
        return complaintRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    @Transactional
    public void markDone(long id) {
        Complaint c = complaintRepository.findById(id).orElseThrow(() -> new BusinessException("记录不存在"));
        c.setStatus(1);
        complaintRepository.save(c);
    }
}
