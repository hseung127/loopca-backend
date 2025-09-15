package com.loopca.app.dto;

import com.loopca.app.entity.StudyCardState;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyCardResponse {
    private Long flashCardIdx;
    private String frontText;
    private String backText;
    private boolean starred;

    public static StudyCardResponse from(StudyCardState state) {
        return StudyCardResponse.builder()
                .flashCardIdx(state.getFlashCard().getIdx())
                .frontText(state.getFlashCard().getFrontText())
                .backText(state.getFlashCard().getBackText())
                .starred(state.isStarred())
                .build();
    }
}