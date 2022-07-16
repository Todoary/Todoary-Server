package com.ms.umc.todoary.utils;



import com.ms.umc.todoary.src.base.BaseException;

import com.ms.umc.todoary.src.security.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

import static com.ms.umc.todoary.src.base.BaseResponseStatus.*;

@Slf4j
@Service
public class JwtService implements InitializingBean {

    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds,
                      CustomUserDetailsService userDetailsService) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /*
    JWT 생성
    @param userIdx
    @return String
     */
    public String createJwt(String email){
        Date now = new Date();
        Date validity = new Date(System.currentTimeMillis() + tokenValidityInMilliseconds);
        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /*
    JWT에서 email 추출
    @return String
    @throws BaseException
     */
    public String getEmail(String accessToken) throws BaseException{
        //1. JWT 추출
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }
        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }
        // 3. email 추출
        return claims.getBody().get("email",String.class);
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("잘못된 JWT 서명입니다.");
        }catch(ExpiredJwtException e){
            log.info("만료된 JWT 입니다.");
        }catch(UnsupportedJwtException e){
            log.info("지원되지 않는 JWT입니다.");
        }catch(IllegalArgumentException e){
            log.info("JWT 토큰이 잘못되었습니다.");
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Authentication getAuthentication(String token) throws BaseException {
        log.info("get Authentication...");
        UserDetails userDetails = userDetailsService.loadUserByUsername(getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities());
    }
}
