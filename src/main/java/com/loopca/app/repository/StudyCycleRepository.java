package com.loopca.app.repository;

import com.loopca.app.entity.StudyCycle;
import com.loopca.app.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyCycleRepository extends JpaRepository<StudyCycle, Long> {
    Optional<StudyCycle> findByStudySessionAndCycleNo(StudySession studySession, int cycleNo);

    Optional<StudyCycle> findTopByStudySessionOrderByCycleNoDesc(StudySession session);

    Optional<Object> findByStudySession_IdxAndCycleNo(Long sessionIdx, int cycleNo);
}
