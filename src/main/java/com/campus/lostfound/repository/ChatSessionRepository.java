package com.campus.lostfound.repository;

import com.campus.lostfound.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findByParticipantAAndParticipantB(Long a, Long b);

    @Query("SELECT s FROM ChatSession s WHERE s.participantA = :uid OR s.participantB = :uid ORDER BY COALESCE(s.lastTime, s.createdAt) DESC")
    List<ChatSession> findSessionsForUser(@Param("uid") Long userId);
}
