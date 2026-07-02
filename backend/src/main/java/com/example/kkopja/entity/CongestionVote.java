package com.example.kkopja.entity;

import com.example.kkopja.constant.CongestionLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "congestion_vote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CongestionVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🎯 Integer 대신 실제 User 엔티티와 다대일 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // 🎯 Integer 대신 실제 Cafe 엔티티와 다대일 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Enumerated(EnumType.STRING)
    @Column(name = "congestion_level", nullable = false)
    private CongestionLevel congestionLevel;

    @Column(name = "created_at", nullable = false) // 갱신 시 업데이트되어야 하므로 updatable = false 제거
    private LocalDateTime createdAt;

    public static CongestionVote createVote(User user, Cafe cafe, CongestionLevel level) {
        CongestionVote vote = new CongestionVote();
        vote.user = user;
        vote.cafe = cafe;
        vote.congestionLevel = level;
        vote.createdAt = LocalDateTime.now();
        return vote;
    }

    public void updateCongestionLevel(CongestionLevel newLevel) {
        this.congestionLevel = newLevel;
        this.createdAt = LocalDateTime.now(); // 투표 시간을 현재 시간으로 최신화
    }
}