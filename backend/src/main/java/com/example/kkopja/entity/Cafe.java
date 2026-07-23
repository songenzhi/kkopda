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

    private String address;

    @Column(name = "has_outlet")
    private Boolean hasOutlet;

    private Boolean wifi;

    private Boolean parking;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "latitude")
    private Double latitude;   // 위도 (Y좌표)

    @Column(name = "longitude")
    private Double longitude;  // 경도 (X좌표)

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CongestionVote> congestionVotes = new ArrayList<>();

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
