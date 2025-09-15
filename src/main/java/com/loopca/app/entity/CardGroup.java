package com.loopca.app.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(length = 255)
    private String description;

    // 멤버 소유 관계 (1:N)
    // Member와 연관관계는 유지하되, DB에 FK 제약은 없음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)) // FK 생성 안 함
    private Member member;

    private LocalDateTime deletedAt;
}

