package com.loopca.app.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardGroupResponse {
    private Long idx;
    private String groupName;
    private String description;
    private LocalDateTime updatedAt;
}
