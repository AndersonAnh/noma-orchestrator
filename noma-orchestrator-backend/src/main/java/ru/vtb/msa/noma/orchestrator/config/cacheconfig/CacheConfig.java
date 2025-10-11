package ru.vtb.msa.noma.orchestrator.config.cacheconfig;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
@EnableCaching
public class CacheConfig {
    @Value("${spring.cache.expireAfterWrite}")
    private static final Integer expireAfterWrite = 24;
    @Value("${spring.cache.maximumSize}")
    private static final Integer maximumSize = 98;

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(expireAfterWrite))
                .maximumSize(maximumSize)
                .removalListener((key, value, cause) ->
                        System.out.printf("Cache expired: key=%s, cause=%s%n", key, cause))
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(caffeine);
        return manager;
    }
}
