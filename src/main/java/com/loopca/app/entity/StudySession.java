package com.loopca.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Study_session")
@Getter
@Setter
public class StudySession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;   // 유저와 연결

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_group_idx", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CardGroup cardGroup;

    @Column(nullable = false, length = 20)
    private String mode;     // smart / normal

    @Column(nullable = false, length = 20)
    private String orderType; // sequential / random

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean completed;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean rewardReceived; // 세션 단위 보상

    private LocalDateTime deletedAt;
}