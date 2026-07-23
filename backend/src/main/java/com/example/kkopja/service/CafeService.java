package com.example.kkopja.service;

import com.example.kkopja.constant.CongestionLevel;
import com.example.kkopja.entity.Cafe;
import com.example.kkopja.repository.CafeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CafeService {

    private final CafeRepository cafeRepository;
    private final CongestionVoteService voteService;

    // 카페 전체 조회
    public List<Cafe> getCafes() {
        List<Cafe> cafes = cafeRepository.findAll();

        // 반복문을 돌며 각 카페의 최근 1시간 혼잡도를 계산하여 채워넣습니다.
        for (Cafe cafe : cafes) {
            CongestionLevel currentCongestion = voteService.getCurrentCongestion(cafe.getId());
            cafe.setCongestionLevel(currentCongestion);
        }

        return cafes;
    }

    // 카페 상세 조회
    public Cafe getCafeById(Integer id) {
        Cafe cafe = cafeRepository.findById(id).orElse(null);
        if (cafe != null) {
            CongestionLevel currentCongestion = voteService.getCurrentCongestion(cafe.getId());
            cafe.setCongestionLevel(currentCongestion);
        }
        return cafe;
    }

    // 카페 추가
    public Cafe createCafe(Cafe cafe) {

        if (cafeRepository.existsByName(cafe.getName())) {
            throw new RuntimeException("ALREADY_EXISTS");
        }
        return cafeRepository.save(cafe);
    }

    // 카페 정보 수정
    @Transactional
    public Cafe updateCafe(Integer id, Cafe cafe) {

        Cafe existingCafe = cafeRepository.findById(id).orElseThrow(() -> new RuntimeException("카페를 찾을 수 없습니다."));
        existingCafe.setName(cafe.getName());
        existingCafe.setAddress(cafe.getAddress());
        existingCafe.setHasOutlet(cafe.getHasOutlet());
        existingCafe.setWifi(cafe.getWifi());
        existingCafe.setParking(cafe.getParking());

        return cafeRepository.save(existingCafe);
    }

    // 카페 정보 삭제
    public void deleteCafe(Integer id) {
        cafeRepository.deleteById(id);
    }

    public Optional<Cafe> findByKakaoId(String kakaoId) {
        return cafeRepository.findByKakaoId(kakaoId);
    }

}
