package com.todoary.ms.src.common.auth.jwt.filter;

import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.legacy.user.LegacyUserProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final LegacyUserProvider legacyUserProvider;
    private final AuthenticationManager authenticationManager;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider, LegacyUserProvider legacyUserProvider, AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.legacyUserProvider = legacyUserProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request.getRequestURI().startsWith("/auth")
            || request.getRequestURI().startsWith("/favicon")
            || request.getRequestURI().startsWith("/swagger")){ // "/auth/*" uri들은 jwt체크 불필요
            chain.doFilter(request,response);
            return;
        }
        String jwtHeader = request.getHeader("Authorization");
        String requestUri = request.getRequestURI();
        if (StringUtils.hasText(jwtHeader)) {
            try {
                Jwts
                        .parserBuilder().setSigningKey(jwtTokenProvider.getAccessKey()).build()
                        .parseClaimsJws(jwtHeader);

                Long user_id = Long.parseLong(jwtTokenProvider.getUserIdFromAccessToken(jwtHeader));

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
            } catch (ExpiredJwtException e) {
                log.info("토큰이 만료됨, uri: {} {}", request.getMethod(), requestUri);
            } catch (Exception e) {
                log.info("토큰이 유효하지 않음, uri: {} {}", request.getMethod(), requestUri);
            }
        }
        chain.doFilter(request, response);
    }
}
