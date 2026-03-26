package com.campus.lostfound.repository;

import com.campus.lostfound.entity.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Page<Complaint> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
