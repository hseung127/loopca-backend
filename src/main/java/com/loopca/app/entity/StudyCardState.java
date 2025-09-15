package com.loopca.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "study_card_state",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"study_cycle_idx", "flash_card_idx"})
        }
)
@Getter
@Setter
public class StudyCardState extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;   // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_cycle_idx", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private StudyCycle studyCycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_card_idx", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private FlashCard flashCard;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean starred;

    private LocalDateTime deletedAt;
}
