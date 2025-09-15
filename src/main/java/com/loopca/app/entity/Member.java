package com.loopca.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, unique = true, length = 50)
    private String memberId;  // 로그인 아이디

    @Column(nullable = false, length = 255)
    private String password; // bcrypt 해시

    @Column(nullable = false, length = 50)
    private String nickname;

    @Builder.Default //초기값 설정 유지
    @Column(nullable = false)
    private Integer points = 0;

    private LocalDateTime deletedAt;

    public void addPoints(int rewardPoint) {
        this.points += rewardPoint;
    }

    public int getPoints() {
        return points;
    }

}