package com.loopca.app.controller;

import com.loopca.app.dto.*;
import com.loopca.app.entity.*;
import com.loopca.app.repository.*;
import com.loopca.app.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudySessionRepository studySessionRepository;
    private final StudyCycleRepository studyCycleRepository;
    private final FlashCardRepository flashCardRepository;
    private final StudyCardStateRepository studyCardStateRepository;
    private final CardGroupRepository cardGroupRepository;
    private final MemberRepository memberRepository;

    private final StudyService studyService;

    /**
     * 마지막 미완료 세션 조회
     */
    @GetMapping("/lastSession")
    public ResponseEntity<StudySessionResponse> getLastUnfinishedSession(
            @AuthenticationPrincipal Long memberIdx, // JwtFilter에서 넣은 memberId 꺼냄
            @RequestParam Long groupIdx) {

        // 마지막 미완료 세션 조회
        Optional<StudySession> optional = studySessionRepository
                .findTopByMember_IdxAndCardGroup_IdxAndCompletedFalseOrderByCreatedAtDesc(memberIdx, groupIdx);

        if (optional.isEmpty()) {
            return ResponseEntity.ok().body(null);
        }

        StudySession session = optional.get();

        // 마지막 사이클 조회
        Optional<StudyCycle> lastCycleOpt = studyCycleRepository
                .findTopByStudySessionOrderByCycleNoDesc(session);

        if (lastCycleOpt.isEmpty()) {
            return ResponseEntity.ok().body(null); // 사이클 자체가 없으면 이어하기 불가, 새로시작
        }

        StudyCycle lastCycle = lastCycleOpt.get();

        // 해당 사이클에 카드 상태 존재 여부 확인
        boolean hasStates = studyCardStateRepository.existsByStudyCycle(lastCycle);
        if (!hasStates) {
            return ResponseEntity.ok().body(null); // 사이클만 있고 상태 없는 경우도 이어하기 불가, 새로시작
        }

        // 정상적으로 이어가기 가능한 세션 응답
        return ResponseEntity.ok(
                StudySessionResponse.from(session, lastCycle.getCycleNo())
        );

    }

    /**
     * 새 세션 생성
     */
    @PostMapping("/session")
    public ResponseEntity<Long> createSession(
            @RequestBody StudySessionRequest request,
            @AuthenticationPrincipal Long memberIdx) {

        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        CardGroup group = cardGroupRepository.findById(request.getGroupIdx())
                .orElseThrow(() -> new RuntimeException("CardGroup not found"));

        StudySession session = new StudySession();
        session.setMember(member);
        session.setCardGroup(group);
        session.setMode(request.getMode());
        session.setOrderType(request.getOrderType());
        session.setCompleted(false);

        StudySession saved = studySessionRepository.save(session);

        return ResponseEntity.ok(saved.getIdx());
    }

    /**
     * 학습 사이클 시작
     */
    @GetMapping("/{studySessionIdx}/cycle/{cycleNo}")
    public ResponseEntity<CycleStartResponse> getCycleCards(
            @PathVariable Long studySessionIdx,
            @PathVariable int cycleNo) {

        // 세션 조회 (없으면 예외 발생)
        StudySession session = studySessionRepository.findById(studySessionIdx)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 사이클 조회 (없으면 새로 생성)
        StudyCycle cycle = studyCycleRepository
                .findByStudySessionAndCycleNo(session, cycleNo)
                .orElseGet(() -> {
                    StudyCycle newCycle = new StudyCycle();
                    newCycle.setStudySession(session);
                    newCycle.setCycleNo(cycleNo);
                    return studyCycleRepository.save(newCycle);
                });

        // 해당 사이클에 연결된 카드 상태 불러오기
        List<StudyCardState> cardStates = studyCardStateRepository.findByStudyCycle(cycle);

        // 4. 사이클별 초기화 로직
        if (cardStates.isEmpty()) {
            if (cycleNo == 1) {
                // 첫 번째 사이클: 그룹의 모든 카드 삽입
                List<FlashCard> cards = flashCardRepository.findByCardGroup_Idx(session.getCardGroup().getIdx());

                cardStates = cards.stream()
                        .map(card -> {
                            StudyCardState state = new StudyCardState();
                            state.setStudyCycle(cycle);
                            state.setFlashCard(card);
                            state.setStarred(false);
                            return studyCardStateRepository.save(state);
                        })
                        .toList();
            } else {
                // 두 번째 이상 사이클: 이전 사이클의 starred=true 카드만 삽입
                StudyCycle prevCycle = studyCycleRepository
                        .findByStudySessionAndCycleNo(session, cycleNo - 1)
                        .orElseThrow(() -> new RuntimeException("Previous cycle not found"));

                List<StudyCardState> prevStarred = studyCardStateRepository
                        .findByStudyCycleAndStarredTrue(prevCycle);

                cardStates = prevStarred.stream()
                        .map(prevState -> {
                            StudyCardState newState = new StudyCardState();
                            newState.setStudyCycle(cycle);
                            newState.setFlashCard(prevState.getFlashCard());
                            newState.setStarred(false); // 새 사이클이니까 초기값은 false
                            return studyCardStateRepository.save(newState);
                        })
                        .toList();
            }
        }

        // 상태 엔티티들을 응답 DTO로 변환
        List<StudyCardResponse> cardList = cardStates.stream()
                .map(StudyCardResponse::from)
                .toList();

        // 응답 객체 생성 (세션 ID, 사이클 번호, 카드 리스트)
        CycleStartResponse response = CycleStartResponse.builder()
                .studySessionIdx(studySessionIdx)
                .cycleNo(cycleNo)
                .cards(cardList)
                .build();

        // JSON 응답 반환
        return ResponseEntity.ok(response);
    }

    /**
     * 학습 사이클 종료
     */
    @PostMapping("/{studySessionIdx}/cycle/{cycleNo}/end")
    public ResponseEntity<CycleEndResponse> endCycle(
            @PathVariable Long studySessionIdx,
            @PathVariable int cycleNo,
            @RequestBody List<StudyCardRequest> updateList) {
        return ResponseEntity.ok(studyService.endCycle(studySessionIdx, cycleNo, updateList));
    }
    /*
    @PostMapping("/{studySessionIdx}/cycle/{cycleNo}/end")
    public ResponseEntity<CycleEndResponse> endCycle(
            @PathVariable Long studySessionIdx,
            @PathVariable int cycleNo,
            @RequestBody List<StudyCardRequest> updateList) {

        StudySession session = studySessionRepository.findById(studySessionIdx)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        StudyCycle cycle = studyCycleRepository
                .findByStudySessionAndCycleNo(session, cycleNo)
                .orElseThrow(() -> new RuntimeException("Cycle not found"));

        List<Long> flashCardIds = updateList.stream()
                .map(StudyCardRequest::getFlashCardIdx)
                .toList();

        // 사이클idx + 카드리스트 조회
        List<StudyCardState> states = studyCardStateRepository
                .findByStudyCycleAndFlashCard_IdxIn(cycle, flashCardIds);

        // Map으로 변환
        Map<Long, Boolean> updateMap = updateList.stream()
                .collect(Collectors.toMap(
                        StudyCardRequest::getFlashCardIdx,
                        StudyCardRequest::isStarred
                ));

        // 프론트 데이터로 DB 엔티티 상태 업데이트
        for (StudyCardState state : states) {
            Boolean starred = updateMap.get(state.getFlashCard().getIdx());
            if (starred != null) {
                state.setStarred(starred);
            }
        }

        // saveAll로 한 번에 반영
        studyCardStateRepository.saveAll(states);

        // 다음 사이클 존재 여부 체크
        boolean hasNext = studyCardStateRepository.existsByStudyCycleAndStarredTrue(cycle);

        if (!hasNext) {
            session.setCompleted(true);
            studySessionRepository.save(session);
        }

        CycleEndResponse response = CycleEndResponse.builder()
                .studySessionIdx(studySessionIdx)
                .cycleNo(cycleNo)
                .hasNextCycle(hasNext)
                .build();

        return ResponseEntity.ok(response);
    }*/

    @GetMapping("/complete")
    public ResponseEntity<CompleteResponse> getCompleteSummary(
            @RequestParam Long sessionIdx,
            @RequestParam int cycleNo) {
        return ResponseEntity.ok(studyService.getCompleteSummary(sessionIdx, cycleNo));
    }

    @PostMapping("/reward")
    public ResponseEntity<RewardResponse> claimReward(
            @RequestParam Long sessionIdx,
            @AuthenticationPrincipal Long memberIdx) {
        return ResponseEntity.ok(studyService.claimReward(sessionIdx, memberIdx));
    }
}
