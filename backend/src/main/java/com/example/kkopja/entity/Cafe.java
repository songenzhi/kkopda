package com.example.kkopja.entity;

import com.example.kkopja.constant.CongestionLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cafes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"reviews"})
public class Cafe {

    private CongestionLevel congestionLevel;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "kakao_id", unique = true)
    private String kakaoId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "has_outlet", nullable = false)
    private Boolean hasOutlet;

    @Column(nullable = false)
    private Boolean wifi;

    @Column(nullable = false)
    private Boolean parking;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "latitude")
    private Double latitude;   // 위도 (Y좌표)

    @Column(name = "longitude")
    private Double longitude;  // 경도 (X좌표)

    // 🎯 추가할 옵션: cascade = CascadeType.REMOVE (또는 ALL), orphanRemoval = true
    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CongestionVote> congestionVotes = new ArrayList<>();

    // 💡 만약 카페에 '리뷰(Review)' 데이터도 달려있다면, 리뷰 쪽도 똑같이 설정해야 에러가 안 납니다!
    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void setCongestionLevel(CongestionLevel congestionLevel) {
        this.congestionLevel = congestionLevel;
    }
}
