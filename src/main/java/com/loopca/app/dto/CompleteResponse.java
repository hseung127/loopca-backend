package com.loopca.app.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompleteResponse {
    private String groupName;
    private int totalCards;
    private boolean rewardReceived; // 세션 보상 수령 여부
}