package com.todoary.ms.src.auth.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.todoary.ms.util.Secret.*;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;


    // JWT 토큰 생성
    public String createAccessToken(Long userid) {
        Claims claims = Jwts.claims().setSubject(userid.toString()); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JWT_ACCESS_TOKEN_EXPTIME)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, JWT_ACCESS_SECRET_KEY)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }
    public String createRefreshToken(Long userid) {
        Claims claims = Jwts.claims().setSubject(userid.toString()); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JWT_REFRESH_TOKEN_EXPTIME)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, JWT_REFRESH_SECRET_KEY)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

//    // JWT 토큰에서 인증 정보 조회
//    public Authentication getAuthentication(String token) {
//        PrincipalDetails userDetails = (PrincipalDetails) userDetailsService.loadUserByUsername(this.getUserid(token));
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }

    // 토큰에서 회원 정보 추출
    public String getUseridFromAcs(String token) {
        return Jwts.parser().setSigningKey(JWT_ACCESS_SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }
    public String getUseridFromRef(String token) {
        return Jwts.parser().setSigningKey(JWT_REFRESH_SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }



}