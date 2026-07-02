package com.example.kkopja.dto;

import com.example.kkopja.constant.CongestionLevel;
import com.example.kkopja.entity.Cafe;
import lombok.Getter;

@Getter
public class CafeResponseDto {
    private Integer id;
    private String name;
    private String address;
    private Boolean hasOutlet;
    private Boolean wifi;
    private Boolean parking;
    private Double rating;
    // 🎯 프론트엔드가 요구하는 혼잡도 필드 명시적으로 추가!
    private String congestionLevel;

    // 🎯 기존 생성자를 수정하여 Cafe 엔티티 정보와 함께 혼잡도를 주입받도록 변경합니다.
    public CafeResponseDto(Cafe cafe, Double rating, CongestionLevel level) {
        this.id = cafe.getId();
        this.name = cafe.getName();
        this.address = cafe.getAddress();
        this.hasOutlet = cafe.getHasOutlet();
        this.wifi = cafe.getWifi();
        this.parking = cafe.getParking();
        this.rating = rating;
        // level이 null일 경우를 대비해 방어 코드를 넣어줍니다.
        this.congestionLevel = level != null ? level.name() : "NORMAL";
    }

    public CafeResponseDto(Cafe cafe, CongestionLevel level) {
        this(cafe, 0.0, level);
    }

    public CafeResponseDto(Cafe cafe) {
        this(cafe, 0.0, CongestionLevel.NORMAL);
    }

}