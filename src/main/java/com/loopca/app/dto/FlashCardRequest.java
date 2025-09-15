package com.loopca.app.dto;

import lombok.Data;

@Data
public class FlashCardRequest {
    private String frontText;
    private String backText;
}