package com.example.kkopja.dto;

import com.example.kkopja.entity.Cafe;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CafeDetailResponseDto {
    private Long id;
    private String name;
    private String address;
    private Boolean hasOutlet;
    private Double rating;

    private Boolean wifi;
    private Boolean parking;

    private List<ReviewResponseDto> reviews;

    public CafeDetailResponseDto(Cafe cafe) {
        this.id = Long.valueOf(cafe.getId());
        this.name = cafe.getName();
        this.address = cafe.getAddress();
        this.hasOutlet = cafe.getHasOutlet();
        this.wifi = cafe.getWifi();
        this.parking = cafe.getParking();
        if (cafe.getReviews() != null && !cafe.getReviews().isEmpty()) {
            // 리뷰 리스트를 쫙 돌면서 별점만 뽑아낸 다음 평균을 구함!
            double average = cafe.getReviews().stream()
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);

            // 소수점 첫째 자리까지만 예쁘게 자르기 (예: 4.3333 -> 4.3)
            this.rating = Math.round(average * 10) / 10.0;
        } else {
            // 리뷰가 하나도 없으면 0.0점
            this.rating = 0.0;

        }


        // 🌟 여기가 핵심! Cafe 안에 있는 Review 엔티티들을 방금 만든 ReviewResponseDto로 변환해서 담아줌
        if (cafe.getReviews() != null) {
            this.reviews = cafe.getReviews().stream()
                    .map(ReviewResponseDto::new)
                    .collect(Collectors.toList());
        }
    }
}