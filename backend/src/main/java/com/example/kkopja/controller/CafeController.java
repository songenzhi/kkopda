package com.example.kkopja.controller;

import com.example.kkopja.constant.CongestionLevel;
import com.example.kkopja.dto.CafeDetailResponseDto;
import com.example.kkopja.dto.CafeResponseDto;
import com.example.kkopja.entity.Cafe;
import com.example.kkopja.repository.CafeRepository;
import com.example.kkopja.repository.ReviewRepository;
import com.example.kkopja.service.CafeService;
import com.example.kkopja.service.CongestionVoteService;
import com.example.kkopja.service.KakaoApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
// 🎯 1. 기본 주소를 "/api/cafes" 로 확정! (RESTful 설계의 정석입니다)
@RequestMapping("/cafes")
@CrossOrigin(origins = "http://localhost:5173")
public class CafeController {

    private final ReviewRepository reviewRepository;
    private final CafeService cafeService;
    private final CafeRepository cafeRepository;
    private final CongestionVoteService voteService;

    // 🎯 2. 기본 주소가 /api/cafes 니까, 여기는 "/nearby"만 쓰면 합쳐져서 /api/cafes/nearby 가 됩니다.
    @GetMapping("/nearby")
    public ResponseEntity<List<Cafe>> getNearbyCafes(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "1.0") double radius
    ) {
        List<Cafe> nearbyCafes = cafeRepository.findNearbyCafes(lat, lon, radius);
        return ResponseEntity.ok(nearbyCafes);
    }

    // 🎯 3. 기본 주소가 /api/cafes 니까, 그대로 두면 합쳐져서 /api/cafes 가 됩니다. (리스트 조회 완벽 매칭!)
    @GetMapping
    public ResponseEntity<Page<CafeResponseDto>> getCafes(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Cafe> cafePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            cafePage = cafeRepository.findByNameContaining(keyword, pageable);
        } else {
            cafePage = cafeRepository.findAll(pageable);
        }

        Page<CafeResponseDto> responsePage = cafePage.map(cafe -> {
            CongestionLevel level = voteService.getCurrentCongestion(cafe.getId());

            Double rating = reviewRepository.getAverageRating(cafe.getId());

            if (rating == null) {
                rating = 0.0;
            } else {
                rating = Math.round(rating * 10) / 10.0; // 소수 첫째 자리
            }

            return new CafeResponseDto(cafe, rating, level);
        });

        return ResponseEntity.ok(responsePage);
    }

    // 🔍 1. 상세 조회 -> 최종 주소: /api/cafes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CafeDetailResponseDto> getCafeById(@PathVariable Integer id) {
        Cafe cafe = cafeService.getCafeById(id);
        return ResponseEntity.ok(new CafeDetailResponseDto(cafe));
    }

    // ✍️ 2. 등록 -> 최종 주소: /api/cafes (POST)
    @PostMapping
    public ResponseEntity<CafeResponseDto> createCafe(@RequestBody Cafe cafe) {
        Cafe savedCafe = cafeService.createCafe(cafe);
        return ResponseEntity.ok(new CafeResponseDto(savedCafe));
    }

    // 🔄 3. 수정 -> 최종 주소: /api/cafes/{id} (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<CafeResponseDto> updateCafe(@PathVariable Integer id, @RequestBody Cafe cafe) {
        Cafe updatedCafe = cafeService.updateCafe(id, cafe);
        return ResponseEntity.ok(new CafeResponseDto(updatedCafe));
    }

    // 삭제 -> 최종 주소: /api/cafes/{id} (DELETE)
    @DeleteMapping("/{id}")
    public void deleteCafe(@PathVariable Integer id) {
        cafeService.deleteCafe(id);
    }

    // 🎯 중복 확인 -> 최종 주소: /api/cafes/check/{kakaoId}
    @GetMapping("/check/{kakaoId}")
    public ResponseEntity<CafeResponseDto> checkDuplicate(@PathVariable String kakaoId) {
        Optional<Cafe> cafe = cafeService.findByKakaoId(kakaoId);
        return cafe.map(c -> ResponseEntity.ok(new CafeResponseDto(c)))
                .orElse(ResponseEntity.noContent().build());
    }
}