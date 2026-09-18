package com.torotech.enterpriseops.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // strength 12 → ~250ms por hash. Balance entre seguridad y UX.
        return new BCryptPasswordEncoder(12);
    }
}