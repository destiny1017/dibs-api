package com.ably.dibs_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class CustomBeanConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // DB 패스워드 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Ably DibsAPI Swagger")
                .description("DibsAPI REST API")
                .version("1.0.0");
    }

}
