package com.loopca.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "Study_cycle",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_session_cycle",
                        columnNames = {"study_session_idx", "cycle_no"}
                )
        }
)
@Setter
@Getter
public class StudyCycle extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_session_idx", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private StudySession studySession;

    @Column(nullable = false)
    private int cycleNo; // 1, 2, 3...

    private LocalDateTime deletedAt;
}
