package com.todoary.ms.src.common.auth.jwt;

import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.web.dto.ClientSecretHeaderParam;
import com.todoary.ms.src.web.dto.ClientSecretPayloadParam;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.todoary.ms.src.common.response.BaseResponseStatus.EXPIRED_JWT;
import static com.todoary.ms.src.common.response.BaseResponseStatus.INVALID_JWT;

@Component
@Getter
public class JwtTokenProvider {
    private final Long JWT_ACCESS_TOKEN_EXPTIME;
    private final Long JWT_REFRESH_TOKEN_EXPTIME;
    private final String  JWT_ACCESS_SECRET_KEY;
    private final String  JWT_REFRESH_SECRET_KEY;
    private Key accessKey;
    private Key refreshKey;

    public JwtTokenProvider(@Value("${jwt.time.access}") Long JWT_ACCESS_TOKEN_EXPTIME,
                            @Value("${jwt.time.refresh}") Long JWT_REFRESH_TOKEN_EXPTIME,
                            @Value("${jwt.secret.access}") String JWT_ACCESS_SECRET_KEY,
                            @Value("${jwt.secret.refresh}") String JWT_REFRESH_SECRET_KEY) {

        this.JWT_ACCESS_TOKEN_EXPTIME = JWT_ACCESS_TOKEN_EXPTIME;
        this.JWT_REFRESH_TOKEN_EXPTIME = JWT_REFRESH_TOKEN_EXPTIME;
        this.JWT_ACCESS_SECRET_KEY = JWT_ACCESS_SECRET_KEY;
        this.JWT_REFRESH_SECRET_KEY = JWT_REFRESH_SECRET_KEY;
    }

    @PostConstruct
    public void initialize() {
        byte[] accessKeyBytes = Decoders.BASE64.decode(JWT_ACCESS_SECRET_KEY);
        this.accessKey = Keys.hmacShaKeyFor(accessKeyBytes);

        byte[] secretKeyBytes = Decoders.BASE64.decode(JWT_REFRESH_SECRET_KEY);
        this.refreshKey = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    // JWT 토큰 생성
    public String createAccessToken(Long userid) {
        Claims claims = Jwts.claims().setSubject(userid.toString()); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JWT_ACCESS_TOKEN_EXPTIME)) // set Expire Time
                .signWith(accessKey, SignatureAlgorithm.HS256)  // 사용할 암호화 알고리즘과
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
                .signWith(refreshKey, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    // 토큰에서 회원 정보 추출
    public String getUserIdFromAccessToken(String token) {
        return getUserIdFromTokenUsingKey(token, accessKey);
    }

    public String getUserIdFromRefreshToken(String token) {
        return getUserIdFromTokenUsingKey(token, refreshKey);
    }

    private String getUserIdFromTokenUsingKey(String token, Key key){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Long getExpirationOfAccessToken(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    // RefreshToken validate
    public void validateRefreshToken(String refreshTokenCode) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(refreshTokenCode);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new TodoaryException(EXPIRED_JWT);
        } catch (Exception exception) {
            throw new TodoaryException(INVALID_JWT);
        }
    }

    // for validate Apple authenticationCode
    public String createAppleClientSecret(ClientSecretHeaderParam clientSecretHeaderParam, ClientSecretPayloadParam clientSecretPayloadParam) {
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", clientSecretHeaderParam.getKeyId());
        jwtHeader.put("alg", clientSecretHeaderParam.getSignature());

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(clientSecretPayloadParam.getIssuer())
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
                .setExpiration(clientSecretPayloadParam.getExpiration()) // 만료 시간
                .setAudience(clientSecretPayloadParam.getAudience())
                .setSubject(clientSecretPayloadParam.getSubject())
                .signWith(clientSecretPayloadParam.getPrivateKey(), SignatureAlgorithm.forName(clientSecretHeaderParam.getSignature()))
                .compact();
    }
}