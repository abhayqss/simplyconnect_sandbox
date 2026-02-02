package com.scnsoft.eldermark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        //TODO according to changes in Spring Security 5, passwords should be reencoded
        return new StandardPasswordEncoder();
    }
}
