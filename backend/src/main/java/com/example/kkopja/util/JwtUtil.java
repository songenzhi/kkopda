package com.example.kkopja.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 실무에서는 application.properties 에 아주 길고 복잡한 문자열로 숨겨둡니다.
    // 임시로 하드코딩된 암호키를 만듭니다. (최소 256bit 길이여야 함)
    private final String secretString = "my-super-secret-key-kkopja-project-very-long-string";
    private final Key key = Keys.hmacShaKeyFor(secretString.getBytes());

    // 토큰 유효시간: 1시간
    private final long expirationMs = 1000 * 60 * 60;

    // 1. 토큰 생성 (로그인 성공 시 호출)
    public String generateToken(Integer userId, String email) {
        return Jwts.builder()
                .setSubject(email)                 // 토큰의 주인 (이메일)
                .claim("userId", userId)         // 커스텀 데이터 (유저 ID)
                .setIssuedAt(new Date())           // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘과 키
                .compact();
    }

    // 2. 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 만료되었거나 변조된 토큰이면 예외 발생
            return false;
        }
    }

    // 3. 토큰에서 Email(Subject) 꺼내기
    // 표준 Claim
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 4. 토큰에서 UserId 꺼내기
    // 내가 만든 Claim
    public Integer getUserIdFromToken(String token) {
        return getClaims(token).get("userId", Integer.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}