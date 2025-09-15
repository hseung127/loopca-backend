package com.loopca.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flash_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 255)
    private String frontText;

    @Column(nullable = false, length = 255)
    private String backText;

    // 그룹 소속 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cardgroup_idx", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CardGroup cardGroup;

    private LocalDateTime deletedAt;
}

