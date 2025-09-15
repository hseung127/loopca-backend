package com.loopca.app.dto;

import com.loopca.app.entity.StudySession;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudySessionResponse {
    private Long studySessionIdx;
    private Long cardGroupIdx;
    private String mode;
    private String orderType;
    private boolean completed;
    private int lastCycleNo;

    public static StudySessionResponse from(StudySession session, int lastCycleNo) {
        return StudySessionResponse.builder()
                .studySessionIdx(session.getIdx())
                .cardGroupIdx(session.getCardGroup().getIdx())
                .mode(session.getMode())
                .orderType(session.getOrderType())
                .completed(session.isCompleted())
                .lastCycleNo(lastCycleNo)
                .build();
    }
}
