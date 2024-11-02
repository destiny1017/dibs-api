package com.ably.dibs_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableJpaAuditing
public class CustomBeanConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // DB 패스워드 암호화
        return new BCryptPasswordEncoder();
    }

}
