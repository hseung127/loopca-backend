package com.loopca.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyCardRequest {
    private Long flashCardIdx;
    private boolean starred;
}