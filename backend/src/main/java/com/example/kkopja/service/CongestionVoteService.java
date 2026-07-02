package com.example.kkopja.service;

import com.example.kkopja.constant.CongestionLevel;
import com.example.kkopja.entity.Cafe;
import com.example.kkopja.entity.CongestionVote;
import com.example.kkopja.entity.User;
import com.example.kkopja.repository.CongestionVoteRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CongestionVoteService {

    private final CongestionVoteRepository voteRepository;
    private final EntityManager em; // ID만으로 프록시 객체를 빠르게 가져오기 위해 사용

    @Transactional
    public void vote(Integer cafeId, Integer userId, CongestionLevel newLevel) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        // 1. ID값을 이용해 영속성 컨텍스트에서 레퍼런스(프록시) 객체를 가져옵니다. (불필요한 SELECT DB 조회 방지)
        Cafe cafe = em.getReference(Cafe.class, cafeId);
        User user = em.getReference(User.class, userId);


        // 2. 1시간 이내 투표 내역이 존재하는지 확인
        Optional<CongestionVote> existingVote = voteRepository
                .findByCafeAndUserAndCreatedAtAfter(cafe, user, oneHourAgo);

        // ⭐ 3. 만약 존재한다면? 재투표 불가! 에러를 발생시킵니다.
        if (existingVote.isPresent()) {
            throw new IllegalStateException("이미 1시간 이내에 이 카페에 투표하셨습니다. 잠시 후 다시 시도해주세요.");
        }

        // 4. 투표 내역이 없을 때만 정상적으로 새로 저장
        CongestionVote newVote = CongestionVote.createVote(user, cafe, newLevel);
        voteRepository.save(newVote);
    }

    @Transactional(readOnly = true)
    public CongestionLevel getCurrentCongestion(Integer cafeId) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        Cafe cafe = em.getReference(Cafe.class, cafeId);

        // 최근 1시간 동안 이 카페에 등록된 모든 투표 내역 가져오기
        List<CongestionVote> recentVotes = voteRepository.findByCafeAndCreatedAtAfter(cafe, oneHourAgo);

        // 만약 최근 1시간 동안 투표가 없다면 보통(NORMAL) 상태 반환
        if (recentVotes.isEmpty()) {
            return CongestionLevel.NORMAL;
        }

        // 가장 많이 투표된 상태(최빈값) 계산 로직
        return recentVotes.stream()
                .collect(Collectors.groupingBy(CongestionVote::getCongestionLevel, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(CongestionLevel.NORMAL);
    }
}
