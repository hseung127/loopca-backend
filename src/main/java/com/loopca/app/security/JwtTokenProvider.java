package com.loopca.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key signingKey;
    private final long validityInMs;
    private final String secretKey; // secretKey 원본 문자열


    // application.yml 값 주입
    /**
     * JWT 서명 및 검증에 사용할 Key 객체 생성
     * - application.yml에 설정된 secretKey(String)를 바이트 배열로 변환
     * - HMAC-SHA 알고리즘에 맞는 Key 객체로 변환하여 사용
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long validityInMs
    ) {
        this.secretKey = secretKey;
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMs = validityInMs;
    }

    /**
     * 토큰 생성
     *  - subject = 회원 PK (idx)
     */
    public String createToken(Long idx) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(String.valueOf(idx))       // PK(id)를 subject에 저장
                .setIssuedAt(now)                     // 발급 시각
                .setExpiration(validity)              // 만료 시각
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 회원 PK(idx) 추출
     */
    public Long getMemberIdx(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
