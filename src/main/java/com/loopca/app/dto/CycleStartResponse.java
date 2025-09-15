package com.loopca.app.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CycleStartResponse {
    private Long studySessionIdx;
    private int cycleNo;
    private List<StudyCardResponse> cards;
}