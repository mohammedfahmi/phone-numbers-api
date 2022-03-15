package com.jumia.phonenumbersapi.configuration.security;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "endpoint.security.white-list-origins")
public class MyCorsConfiguration implements WebMvcConfigurer {
    private List<String> origins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("add allowed Origins: " + String.join(",", origins));
        registry.addMapping("/**")
                .allowedOrigins(origins.toArray(new String[origins.size()]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                .allowCredentials(true);
    }
}
