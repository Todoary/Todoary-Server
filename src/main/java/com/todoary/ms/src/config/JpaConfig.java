package com.todoary.ms.src.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // Jpa Auditing 활성화 (moved for WebMvcTest)
public class JpaConfig {
}
