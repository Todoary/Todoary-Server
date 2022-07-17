package com.todoary.ms.src.config;

import com.todoary.ms.src.auth.*;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.jwt.config.CustomAccessDeniedHandler;
import com.todoary.ms.src.auth.jwt.config.CustomAuthenticationEntryPoint;
import com.todoary.ms.src.auth.jwt.config.OAuth2SuccessHandler;
import com.todoary.ms.src.auth.jwt.filter.JwtAuthenticationFilter;
import com.todoary.ms.src.auth.jwt.filter.JwtAuthorizationFilter;
import com.todoary.ms.src.user.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2SuccessHandler successHandler;
    private final AuthService authService;
    private final UserProvider userProvider;
    private final PrincipalDetailsService userDetailsService;


    @Autowired
    public SecurityConfig(PrincipalOAuth2UserService principalOAuth2UserService, JwtTokenProvider jwtTokenProvider, OAuth2SuccessHandler successHandler, AuthService authService, UserProvider userProvider, PrincipalDetailsService userDetailsService) {
        this.principalOAuth2UserService = principalOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.successHandler = successHandler;
        this.authService = authService;
        this.userProvider = userProvider;
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, authenticationManager, authService);
        jwtAuthenticationFilter.setFilterProcessesUrl("/auth/signin");

        http
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider, userProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

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
                // swagger
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/health").permitAll()
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
                .successHandler(new OAuth2SuccessHandler(jwtTokenProvider));

        return http.build();
    }
}
