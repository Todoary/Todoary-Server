package com.todoary.ms.src.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing(dateTimeProviderRef = "testDateTimeProvider")
public class TestJpaAuditingConfig {
    @Bean
    public DateTimeProvider testDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }
}
