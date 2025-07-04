package com.tads.credito.config;

import com.tads.credito.decorator.*;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ScoreClient scoreClient(RestTemplate restTemplate, CacheManager cacheManager) {
        // Ordem dos decorators: Cache -> Controle de Requisições -> Cliente real
        return new CachedClient(
                new ControlledClient(
                        new APIClient(restTemplate)
                ),
                cacheManager
        );
    }
}