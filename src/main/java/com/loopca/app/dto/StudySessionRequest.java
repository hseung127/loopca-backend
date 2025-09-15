package com.loopca.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudySessionRequest {
    private Long groupIdx;
    private String mode;      // smart / normal
    private String orderType; // sequential / random
}