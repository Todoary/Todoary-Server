package com.todoary.ms.src.auth.jwt.filter;

import com.todoary.ms.src.auth.PrincipalDetailsService;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.user.UserProvider;

import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.todoary.ms.util.BaseResponseStatus.EXPIRED_JWT;
import static com.todoary.ms.util.BaseResponseStatus.INVALID_JWT;
import static com.todoary.ms.util.Secret.JWT_ACCESS_SECRET_KEY;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserProvider userProvider;
    private final AuthenticationManager authenticationManager;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider, UserProvider userProvider, AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userProvider = userProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request.getRequestURI().startsWith("/auth")
            || request.getRequestURI().startsWith("/favicon")){ // "/auth/*" uri들은 jwt체크 불필요
            log.info("JWT 인증 통과");
            chain.doFilter(request,response);
            return;
        }
        log.info("JWT 인증 시작");
        String jwtHeader = request.getHeader("Authorization");
        String requestUri = request.getRequestURI();
        if (StringUtils.hasText(jwtHeader)){
            try {
                Jwts
                        .parser()
                        .setSigningKey(JWT_ACCESS_SECRET_KEY)
                        .parseClaimsJws(jwtHeader);

                Long user_id = Long.parseLong(jwtTokenProvider.getUseridFromAcs(jwtHeader));
//                log.info("인증 완료, uri: " + requestUri + " 인증 정보: " + authentication.getName());


                Collection<GrantedAuthority> userAuthorities = new ArrayList<>(); // 리팩토링 필요
                userAuthorities.add(new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return "ROLE_USER";
                    }
                });

                Authentication authentication = new UsernamePasswordAuthenticationToken(user_id, "", userAuthorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("user_id", user_id);
            }catch (ExpiredJwtException e) {
                log.info("토큰이 만료됨, uri: " + requestUri);
            } catch (Exception e) {
                log.info("토큰이 유효하지 않음, uri: " + requestUri);
            }
        }else{
            log.info("토큰이 유효하지 않음, uri: " + requestUri);
        }
        chain.doFilter(request, response);

    }


}
