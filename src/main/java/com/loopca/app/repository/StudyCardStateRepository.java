package com.loopca.app.repository;

import com.loopca.app.entity.StudyCardState;
import com.loopca.app.entity.StudyCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudyCardStateRepository extends JpaRepository<StudyCardState, Long> {
    List<StudyCardState> findByStudyCycle(StudyCycle cycle);

    // 카드 상태 존재 여부 확인
    boolean existsByStudyCycle(StudyCycle cycle);

    //Optional<StudyCardState> findByStudyCycleAndFlashCard_Idx(StudyCycle cycle, Long flashCardIdx);

    // 사이클idx + 카드리스트 조회
    List<StudyCardState> findByStudyCycleAndFlashCard_IdxIn(StudyCycle cycle, List<Long> flashCardIdxList);

    boolean existsByStudyCycleAndStarredTrue(StudyCycle cycle);

    List<StudyCardState> findByStudyCycleAndStarredTrue(StudyCycle prevCycle);
}