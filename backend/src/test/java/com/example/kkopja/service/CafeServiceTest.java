package com.example.kkopja.service;

import com.example.kkopja.constant.CongestionLevel;
import com.example.kkopja.entity.Cafe;
import com.example.kkopja.repository.CafeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.kkopja.constant.CongestionLevel.RELAXED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CafeServiceTest {

    @Mock
    private CafeRepository cafeRepository;

    @Mock
    private CongestionVoteService voteService;

    @InjectMocks
    private CafeService cafeService;

    private Cafe sampleCafe;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 임의의 Cafe 객체 초기화 (생성자나 빌더 패턴에 맞게 수정하세요)
        sampleCafe = Cafe.builder()
                .id(1)
                .kakaoId("k_123456")
                .name("테스트 카페")
                .address("서울시 강남구")
                .build();
    }

    @Test
    @DisplayName("모든 카페 목록을 성공적으로 조회한다")
    void getCafes() {
        // given
        List<Cafe> cafeList = Arrays.asList(sampleCafe, new Cafe());
        given(cafeRepository.findAll()).willReturn(cafeList);
        given(voteService.getCurrentCongestion(any())).willReturn((RELAXED));

        // when
        List<Cafe> result = cafeService.getCafes();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cafeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("ID로 특정 카페를 성공적으로 조회한다")
    void getCafeById() {
        // given
        Integer cafeId = 1;
        given(cafeRepository.findById(cafeId)).willReturn(Optional.of(sampleCafe));

        // when
        Cafe result = cafeService.getCafeById(cafeId);

        // then
        assertNotNull(result);
        assertEquals(sampleCafe.getName(), result.getName());
        verify(cafeRepository, times(1)).findById(cafeId);
    }

    @Test
    @DisplayName("새로운 카페를 성공적으로 생성(저장)한다")
    void createCafe() {
        // given
        given(cafeRepository.save(any(Cafe.class))).willReturn(sampleCafe);

        // when
        Cafe createdCafe = cafeService.createCafe(sampleCafe);

        // then
        assertNotNull(createdCafe);
        assertEquals(sampleCafe.getKakaoId(), createdCafe.getKakaoId());
        verify(cafeRepository, times(1)).save(any(Cafe.class));
    }

    @Test
    @DisplayName("기존 카페의 정보를 성공적으로 수정한다")
    void updateCafe() {
        // given
        Integer cafeId = 1;

        Cafe updateParam = Cafe.builder()
                .name("수정된 이름")
                .address("수정된 주소")
                .hasOutlet(true)
                .wifi(true)
                .parking(true)
                .build();

        given(cafeRepository.findById(cafeId)).willReturn(Optional.of(sampleCafe));
        given(cafeRepository.save(any(Cafe.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Cafe updatedCafe = cafeService.updateCafe(cafeId, updateParam);

        // then
        assertEquals("수정된 이름", updatedCafe.getName());
        assertEquals("수정된 주소", updatedCafe.getAddress());
        assertEquals(true, updatedCafe.getHasOutlet());
        assertEquals(true, updatedCafe.getWifi());
        assertEquals(true, updatedCafe.getParking());

        verify(cafeRepository, times(1)).findById(cafeId);
    }

    @Test
    @DisplayName("ID를 통해 카페를 성공적으로 삭제한다")
    void deleteCafe() {
        // given
        Integer cafeId = 1;

        // when
        cafeService.deleteCafe(cafeId);

        // then
         verify(cafeRepository, times(1)).deleteById(cafeId);
    }

    @Test
    @DisplayName("카카오 ID로 카페를 성공적으로 조회한다")
    void findByKakaoId() {
        // given: Repository가 Optional.of(sampleCafe)를 반환하도록 설정
        String kakaoId = "k_123456";
        given(cafeRepository.findByKakaoId(kakaoId)).willReturn(Optional.of(sampleCafe));

        // when: 서비스 메서드를 호출하고 반환값을 Optional<Cafe> 타입으로 받음
        Optional<Cafe> result = cafeService.findByKakaoId(kakaoId);

        // then: Optional 객체에 대한 검증 진행
        assertTrue(result.isPresent()); // 1. 값이 비어있지 않고 잘 들어있는지 확인
        assertEquals(kakaoId, result.get().getKakaoId()); // 2. Optional 안의 Cafe 객체를 꺼내서(.get()) 값 확인
        verify(cafeRepository, times(1)).findByKakaoId(kakaoId); // 3. Repository 메서드가 잘 호출되었는지 확인
    }
}