package com.todoary.ms.src.config;

import com.todoary.ms.src.common.auth.jwt.filter.JwtAuthorizationFilter;
import com.todoary.ms.src.legacy.auth.LegacyAuthService;
import com.todoary.ms.src.common.auth.PrincipalOAuth2UserService;
import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.common.auth.jwt.config.CustomAccessDeniedHandler;
import com.todoary.ms.src.common.auth.jwt.config.CustomAuthenticationEntryPoint;
import com.todoary.ms.src.common.auth.jwt.config.OAuth2SuccessHandler;
import com.todoary.ms.src.legacy.user.LegacyUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LegacyAuthService legacyAuthService;
    private final LegacyUserProvider legacyUserProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(
                new JwtAuthorizationFilter(
                        jwtTokenProvider,
                        legacyUserProvider,
                        authenticationManagerBuilder.getObject()
                ), UsernamePasswordAuthenticationFilter.class);
        
        http.csrf().disable() // 세션 사용 안하므로
                // exception handling 새로 만든 클래스로
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안함
                .and()
                .formLogin().disable() // form 태그 만들어서 로그인을 안함
                .httpBasic().disable() // 기본 방식 안쓰고 Bearer(jwt) 방법 사용할 것
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/oauth2/**").permitAll()
                .antMatchers("/alarm/**").permitAll()
                // swagger
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/health").permitAll()
                // h2 console
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                // for test
                .antMatchers("/test").permitAll()
                // profile check
                .antMatchers("/profile").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                // .antMatchers("/").permitAll()
                .and()
                .oauth2Login()
                // .redirectionEndpoint().baseUri("/auth/token")
                // .and()
                .userInfoEndpoint().userService(principalOAuth2UserService)
                .and()
                .successHandler(new OAuth2SuccessHandler(jwtTokenProvider, legacyAuthService));
        http.headers().frameOptions().sameOrigin();
        return http.build();
    }
}
