package com.todoary.ms.src.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Profile({"release"})
@EnableScheduling
public class SchedulingConfig {
}
