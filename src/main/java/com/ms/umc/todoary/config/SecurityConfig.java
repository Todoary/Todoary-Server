package com.ms.umc.todoary.config;

import com.ms.umc.todoary.src.security.CustomAccessDeniedHandler;
import com.ms.umc.todoary.src.security.CustomAuthenticationEntryPoint;
import com.ms.umc.todoary.src.security.CustomOAuth2UserService;
import com.ms.umc.todoary.src.security.OAuth2SuccessHandler;
import com.ms.umc.todoary.utils.JwtAuthenticationFilter;
import com.ms.umc.todoary.utils.JwtAuthorizationFilter;
import com.ms.umc.todoary.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 필터를 스프링 필터체인에 등록
public class SecurityConfig {
    private final JwtService jwtService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final CustomOAuth2UserService customOauth2UserService;

    @Autowired
    public SecurityConfig(JwtService jwtService, AuthenticationManagerBuilder authenticationManagerBuilder, CustomOAuth2UserService customOauth2UserService) {
        this.jwtService = jwtService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.customOauth2UserService = customOauth2UserService;
    }


    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, authenticationManagerBuilder);
        jwtAuthenticationFilter.setFilterProcessesUrl("/auth/signin");

        http
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(new JwtAuthorizationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

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
                // .antMatchers("/").permitAll()
                // .antMatchers("/favicon.ico").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                // .redirectionEndpoint().baseUri("/auth/token")
                // .and()
                .userInfoEndpoint().userService(customOauth2UserService)
                .and()
                .successHandler(new OAuth2SuccessHandler(jwtService));

        return http.build();
    }

    /**
     * resource 에는 security 적용 안함
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(0)
    public SecurityFilterChain resources(HttpSecurity http) throws Exception {
        http
                .requestMatchers(matchers -> matchers.antMatchers("/resources/**"))
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
                .requestCache().disable()
                .securityContext().disable()
                .sessionManagement().disable();
        return http.build();
    }
}
