package com.beanspot.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final long MAX_AGE_SECS = 3600;

    /**
     * CORS 허용 오리진. 환경변수 CORS_ALLOWED_ORIGINS 에 콤마로 구분해 지정합니다.
     * (예: CORS_ALLOWED_ORIGINS=https://beanspot.app,https://admin.beanspot.app)
     *
     * iOS/Android 네이티브 앱은 Origin 헤더를 보내지 않아 CORS 대상이 아니므로,
     * 이 값은 웹(Expo web, 관리자 페이지 등)에서 접근할 때만 의미가 있습니다.
     * 기본값은 로컬 개발 주소이며, 운영 배포 시 실제 도메인으로 반드시 교체하세요.
     */
    @Value("${cors.allowed-origins:http://127.0.0.1:8081,http://localhost:8081,http://127.0.0.1:5500,http://localhost:5500}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }
}
