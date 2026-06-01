package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许的来源 - 开发环境和生产环境
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:8080",    // 本地开发
            "http://localhost:3000",    // 前端开发服务器
            "http://127.0.0.1:8080"    // 本地访问
        ));

        // 允许的 HTTP 方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许的请求头
        config.setAllowedHeaders(Arrays.asList("*"));

        // 允许携带凭证（如 cookies）
        config.setAllowCredentials(true);

        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);

        // 暴露的响应头
        config.setExposedHeaders(Arrays.asList("Content-Disposition", "Content-Length"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
