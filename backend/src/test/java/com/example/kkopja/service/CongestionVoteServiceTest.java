package com.example.kkopja.service;

import com.example.kkopja.constant.CongestionLevel;
import com.example.kkopja.entity.Cafe;
import com.example.kkopja.entity.CongestionVote;
import com.example.kkopja.entity.User;
import com.example.kkopja.repository.CongestionVoteRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CongestionVoteServiceTest {

    @Mock
    private CongestionVoteRepository voteRepository;

    @Mock
    private EntityManager em; // 💡 EntityManager도 가짜 객체(Mock)로 만듭니다.

    @InjectMocks
    private CongestionVoteService congestionVoteService;

    @Test
    @DisplayName("투표 성공: 1시간 이내 투표 기록이 없으면 정상 저장된다")
    void vote_Success() {
        // given
        Integer cafeId = 1;
        Integer userId = 1;
        CongestionLevel level = CongestionLevel.CROWDED; // 가정: CROWDED 상태가 있다고 가정

        Cafe mockCafe = Cafe.builder().id(cafeId).build();
        User mockUser = User.builder().id(userId).build();

        // 1. EntityManager가 프록시 객체를 반환하도록 설정
        given(em.getReference(Cafe.class, cafeId)).willReturn(mockCafe);
        given(em.getReference(User.class, userId)).willReturn(mockUser);

        // 2. 최근 1시간 이내 투표 내역이 없다고(Optional.empty) 설정
        // 시간을 다룰 때는 정확한 시간 매칭이 어려우므로 any(LocalDateTime.class)를 사용합니다.
        given(voteRepository.findByCafeAndUserAndCreatedAtAfter(eq(mockCafe), eq(mockUser), any(LocalDateTime.class)))
                .willReturn(Optional.empty());

        // when
        congestionVoteService.vote(cafeId, userId, level);

        // then
        verify(voteRepository, times(1)).save(any(CongestionVote.class));
    }

    @Test
    @DisplayName("투표 실패: 1시간 이내 투표 기록이 있으면 예외가 발생한다")
    void vote_Fail_AlreadyVoted() {
        // given
        Integer cafeId = 1;
        Integer userId = 1;

        Cafe mockCafe = Cafe.builder().id(cafeId).build();
        User mockUser = User.builder().id(userId).build();
        CongestionVote existingVote = CongestionVote.builder().build(); // 이미 존재하는 투표 내역

        given(em.getReference(Cafe.class, cafeId)).willReturn(mockCafe);
        given(em.getReference(User.class, userId)).willReturn(mockUser);

        // 최근 투표 내역이 존재한다고 설정
        given(voteRepository.findByCafeAndUserAndCreatedAtAfter(eq(mockCafe), eq(mockUser), any(LocalDateTime.class)))
                .willReturn(Optional.of(existingVote));

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            congestionVoteService.vote(cafeId, userId, CongestionLevel.NORMAL);
        });

        assertEquals("이미 1시간 이내에 이 카페에 투표하셨습니다. 잠시 후 다시 시도해주세요.", exception.getMessage());
        // 예외가 발생했으므로 save는 절대 호출되지 않아야 함
        verify(voteRepository, never()).save(any(CongestionVote.class));
    }

    @Test
    @DisplayName("혼잡도 조회: 최근 1시간 동안 투표가 없으면 NORMAL을 반환한다")
    void getCurrentCongestion_EmptyVotes() {
        // given
        Integer cafeId = 1;
        Cafe mockCafe = Cafe.builder().id(cafeId).build();

        given(em.getReference(Cafe.class, cafeId)).willReturn(mockCafe);
        // 빈 리스트 반환
        given(voteRepository.findByCafeAndCreatedAtAfter(eq(mockCafe), any(LocalDateTime.class)))
                .willReturn(List.of());

        // when
        CongestionLevel result = congestionVoteService.getCurrentCongestion(cafeId);

        // then
        assertEquals(CongestionLevel.NORMAL, result);
    }

    @Test
    @DisplayName("혼잡도 조회: 여러 투표 중 가장 많이 투표된 최빈값을 반환한다")
    void getCurrentCongestion_CalculateMode() {
        // given
        Integer cafeId = 1;
        Cafe mockCafe = Cafe.builder().id(cafeId).build();

        // 혼잡(CROWDED) 2표, 한산(QUIET) 1표라고 가정
        CongestionVote vote1 = CongestionVote.builder().congestionLevel(CongestionLevel.CROWDED).build();
        CongestionVote vote2 = CongestionVote.builder().congestionLevel(CongestionLevel.CROWDED).build();
        CongestionVote vote3 = CongestionVote.builder().congestionLevel(CongestionLevel.RELAXED).build(); // 가정: QUIET 상태 존재

        given(em.getReference(Cafe.class, cafeId)).willReturn(mockCafe);
        given(voteRepository.findByCafeAndCreatedAtAfter(eq(mockCafe), any(LocalDateTime.class)))
                .willReturn(List.of(vote1, vote2, vote3));

        // when
        CongestionLevel result = congestionVoteService.getCurrentCongestion(cafeId);

        // then
        // 혼잡이 2표로 가장 많으므로 CROWDED가 반환되어야 함
        assertEquals(CongestionLevel.CROWDED, result);
    }
}