package com.loopca.app.repository;

import com.loopca.app.entity.FlashCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlashCardRepository extends JpaRepository<FlashCard, Long> {
    List<FlashCard> findByCardGroup_Idx(Long groupId);

    // FlashCard 테이블에서 그룹별 카드 개수 count
    int countByCardGroup_Idx(Long groupIdx);
}