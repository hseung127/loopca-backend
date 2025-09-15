package com.loopca.app.service;

import com.loopca.app.dto.CompleteResponse;
import com.loopca.app.dto.CycleEndResponse;
import com.loopca.app.dto.RewardResponse;
import com.loopca.app.dto.StudyCardRequest;
import com.loopca.app.entity.Member;
import com.loopca.app.entity.StudyCardState;
import com.loopca.app.entity.StudyCycle;
import com.loopca.app.entity.StudySession;
import com.loopca.app.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudySessionRepository studySessionRepository;
    private final StudyCycleRepository studyCycleRepository;
    private final StudyCardStateRepository studyCardStateRepository;
    private final MemberRepository memberRepository;
    private final FlashCardRepository flashCardRepository;

    @Transactional
    public CycleEndResponse endCycle(Long studySessionIdx, int cycleNo, List<StudyCardRequest> updateList) {
        StudySession session = studySessionRepository.findById(studySessionIdx)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        StudyCycle cycle = studyCycleRepository
                .findByStudySessionAndCycleNo(session, cycleNo)
                .orElseThrow(() -> new RuntimeException("Cycle not found"));

        List<Long> flashCardIds = updateList.stream()
                .map(StudyCardRequest::getFlashCardIdx)
                .toList();

        List<StudyCardState> states = studyCardStateRepository
                .findByStudyCycleAndFlashCard_IdxIn(cycle, flashCardIds);

        Map<Long, Boolean> updateMap = updateList.stream()
                .collect(Collectors.toMap(
                        StudyCardRequest::getFlashCardIdx,
                        StudyCardRequest::isStarred
                ));

        for (StudyCardState state : states) {
            Boolean starred = updateMap.get(state.getFlashCard().getIdx());
            if (starred != null) {
                state.setStarred(starred);
            }
        }

        studyCardStateRepository.saveAll(states);

        boolean hasNext = studyCardStateRepository.existsByStudyCycleAndStarredTrue(cycle);

        if (!hasNext) {
            session.setCompleted(true);
            studySessionRepository.save(session);
        }

        return CycleEndResponse.builder()
                .studySessionIdx(studySessionIdx)
                .cycleNo(cycleNo)
                .hasNextCycle(hasNext)
                .build();
    }

    /**
     * 완료 화면 요약
     */
    public CompleteResponse getCompleteSummary(Long sessionIdx, int cycleNo) {
        StudyCycle cycle = (StudyCycle) studyCycleRepository
                .findByStudySession_IdxAndCycleNo(sessionIdx, cycleNo)
                .orElseThrow(() -> new IllegalArgumentException("사이클 없음"));

        List<StudyCardState> states = studyCardStateRepository.findByStudyCycle(cycle);

        // groupIdx 가져오기
        Long groupIdx = cycle.getStudySession().getCardGroup().getIdx();

        // FlashCard 테이블에서 count
        int totalCards = flashCardRepository.countByCardGroup_Idx(groupIdx);

        return CompleteResponse.builder()
                .groupName(cycle.getStudySession().getCardGroup().getGroupName())
                .totalCards(totalCards)
                .rewardReceived(cycle.getStudySession().isRewardReceived())
                .build();
    }

    /**
     * 세션 종료 후 보상 수령
     */
    @Transactional
    public RewardResponse claimReward(Long sessionIdx, Long memberIdx) {
        StudySession session = studySessionRepository.findById(sessionIdx)
                .orElseThrow(() -> new IllegalArgumentException("세션 없음"));

        if (session.isRewardReceived()) {
            return RewardResponse.builder()
                    .message("이미 보상 수령 완료")
                    .rewardPoint(0)
                    .newPointBalance(session.getCardGroup().getMember().getPoints())
                    .build();
        }

        // 보상 정책: 세션 끝날 때 고정 포인트 지급
        int rewardPoint = 100;

        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new IllegalArgumentException("멤버 없음"));
        member.addPoints(rewardPoint);

        session.setRewardReceived(true); // @Transactional 붙이면 commit 시점에 DB UPDATE 자동 발생

        return RewardResponse.builder()
                .message("보상 수령 성공")
                .rewardPoint(rewardPoint)
                .newPointBalance(member.getPoints())
                .build();
    }
}
