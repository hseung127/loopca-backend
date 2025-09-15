package com.loopca.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class CardGroupRequest {
    private String groupName;
    private String description;
    private List<FlashCardRequest> cards; // 엑셀에서 온 카드들
}