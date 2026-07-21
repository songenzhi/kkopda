package com.example.kkopja.service;

import com.example.kkopja.entity.Cafe;
import com.example.kkopja.repository.CafeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CafeRepository cafeRepository;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public KakaoApiService(CafeRepository cafeRepository) {
        this.cafeRepository = cafeRepository;
    }

    public void fetchCafesFromKakao(String keyword) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword + " 카페";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );

            // 1. 카카오가 준 문자열 데이터를 JsonNode 객체로 변환!
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode documents = rootNode.path("documents"); // 카페 목록 배열만 쏙 빼오기

            System.out.println("====== 카카오 카페 데이터 추출 시작 ======");

            // 2. 배열을 돌면서 데이터 추출tm
            for (JsonNode node : documents) {
                String kakaoId = node.path("id").asText(); // 진짜 카카오 고유 숫자 ID
                String cafeName = node.path("place_name").asText();
                String address = node.path("road_address_name").asText();
                if (address.isEmpty()) {
                    address = node.path("address_name").asText();
                }

                if (cafeRepository.findByKakaoId(kakaoId).isEmpty()) {
                    Cafe cafe = Cafe.builder()
                            .kakaoId(kakaoId)
                            .name(cafeName)
                            .address(address)
                            .hasOutlet(false)
                            .wifi(false)
                            .parking(false)
                            .build();

                    cafeRepository.save(cafe);
                    System.out.println("✅ 새 카페 저장 완료: " + cafeName);
                } else {
                    System.out.println("⏩ 이미 존재하는 카페 패스: " + cafeName);
                }
            }
            System.out.println("====== 추출 완료! ======");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("카카오 통신 또는 파싱 실패!");
        }
    }
}