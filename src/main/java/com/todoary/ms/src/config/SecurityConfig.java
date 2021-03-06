package com.todoary.ms.src.config;

import com.todoary.ms.src.auth.AuthService;
import com.todoary.ms.src.auth.PrincipalOAuth2UserService;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.jwt.config.CustomAccessDeniedHandler;
import com.todoary.ms.src.auth.jwt.config.CustomAuthenticationEntryPoint;
import com.todoary.ms.src.auth.jwt.config.OAuth2SuccessHandler;
import com.todoary.ms.src.auth.jwt.filter.JwtAuthorizationFilter;
import com.todoary.ms.src.user.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @Autowired
    public SecurityConfig(PrincipalOAuth2UserService principalOAuth2UserService, JwtTokenProvider jwtTokenProvider, OAuth2SuccessHandler successHandler, AuthService authService, UserProvider userProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.principalOAuth2UserService = principalOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.successHandler = successHandler;
        this.authService = authService;
        this.userProvider = userProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

       // AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);// -> null ??????
//        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, authenticationManagerBuilder, authService);
//        jwtAuthenticationFilter.setFilterProcessesUrl("/auth/signin");

        http
//                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider, userProvider, authenticationManagerBuilder.getObject()),
                        UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable() // ?????? ?????? ????????????
                // exception handling ?????? ?????? ????????????
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ?????? ?????? ??????
                .and()
                .formLogin().disable() // form ?????? ???????????? ???????????? ??????
                .httpBasic().disable() // ?????? ?????? ????????? Bearer(jwt) ?????? ????????? ???
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
                .successHandler(new OAuth2SuccessHandler(jwtTokenProvider, authService));

        return http.build();
    }
}
