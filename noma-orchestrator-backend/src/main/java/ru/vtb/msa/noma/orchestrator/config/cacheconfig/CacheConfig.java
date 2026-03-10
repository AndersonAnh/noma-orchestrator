package ru.vtb.msa.noma.orchestrator.config.cacheconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.vtb.msa.noma.orchestrator.cache.repositorycache.InsuranceLifeCacheRepository;


/**
 * Конфигурация Redis.
 *
 * @EnableRedisRepositories — включает поддержку Spring Data Redis Repository (@RedisHash).
 * Сканирует пакет db.repository и регистрирует бины для CrudRepository<InsuranceLifeCache, String>.
 *
 * includeFilters — ограничивает сканирование только Redis-репозиториями,
 * чтобы не конфликтовать с JPA-репозиториями в том же пакете.
 *
 * considerNestedRepositories = false — не сканирует вложенные интерфейсы.
 *
 * RedisTemplate<String, String> — используется для ручной работы с Redis (дедупликация Kafka в InfoSystemProcessor).
 * InsuranceLifeCacheRepository — использует внутренний RedisKeyValueTemplate (создаётся автоматически).
 */
@Configuration
@EnableRedisRepositories(
        basePackages = "ru.vtb.msa.noma.orchestrator.db.repository",
        includeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                classes = InsuranceLifeCacheRepository.class
        )
)
public class CacheConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }
}
