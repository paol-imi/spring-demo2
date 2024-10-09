package org.example.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration class for CORS.
 */
@Configuration
public class CorsConfig {

    @Value("${app.web.cors.allowed-origins:null}")
    private List<String> allowedOrigins;

    @Value("${app.web.cors.allowed-methods:null}")
    private List<String> allowedMethods;

    @Value("${app.web.cors.allowed-headers:null}")
    private List<String> allowedHeaders;

    @Value("${app.web.cors.allow-credentials:false}")
    private boolean allowCredentials;

    /**
     * Creates a CorsConfigurationSource bean.
     *
     * @return The CorsConfigurationSource bean
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(this.allowCredentials);
        config.setAllowedOrigins(this.allowedOrigins);
        config.setAllowedHeaders(this.allowedHeaders);
        config.setAllowedMethods(this.allowedMethods);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}