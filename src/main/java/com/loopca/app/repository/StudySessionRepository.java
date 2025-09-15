package com.loopca.app.repository;

import com.loopca.app.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    Optional<StudySession> findTopByMember_IdxAndCardGroup_IdxAndCompletedFalseOrderByCreatedAtDesc(Long memberId, Long groupId);
}