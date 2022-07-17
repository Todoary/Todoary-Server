package com.todoary.ms.src.config;

import com.todoary.ms.src.auth.*;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.jwt.config.OAuth2SuccessHandler;
import com.todoary.ms.src.auth.jwt.filter.JwtAuthenticationFilter;
import com.todoary.ms.src.auth.jwt.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2SuccessHandler successHandler;
    private final AuthService authService;


    @Autowired
    public SecurityConfig(PrincipalOAuth2UserService principalOAuth2UserService, JwtTokenProvider jwtTokenProvider, OAuth2SuccessHandler successHandler, AuthService authService) {
        this.principalOAuth2UserService = principalOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.successHandler = successHandler;
        this.authService = authService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, authenticationManagerBean(), authService);
        jwtAuthenticationFilter.setFilterProcessesUrl("/auth/signin");
        http.csrf().disable();

        http
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                .formLogin().disable()
                .oauth2Login()
                .successHandler(successHandler)
                .userInfoEndpoint()
                .userService(principalOAuth2UserService)

                .and()

                .and()
                .addFilter(new JwtAuthorizationFilter(authenticationManagerBean(), jwtTokenProvider));

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
