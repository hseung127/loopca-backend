package com.loopca.app.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardResponse {
    private String message;
    private int rewardPoint;
    private int newPointBalance;
}
