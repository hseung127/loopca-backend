package com.loopca.app.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CycleEndResponse {
    private Long studySessionIdx;
    private int cycleNo;
    private boolean hasNextCycle;
}