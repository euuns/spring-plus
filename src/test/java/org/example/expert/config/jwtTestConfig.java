package org.example.expert.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class jwtTestConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
}
