package ru.vtb.msa.noma.orchestrator.integration.infoservice.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import ru.vtb.msa.OrchestratorInfoResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
@Slf4j
@RequiredArgsConstructor
public class InfoSystemProcessor {

    private final CacheManager cacheManager;

    private static final String CACHE_NAME = "seenMessages";

    /**
     * Проверяет, поступало ли уже сообщение с данным accountId и timestamp в течение последних 24 часов.
     *
     * @return true — если сообщение впервые, false — если уже приходило ранее
     */
    public boolean firstTime(OrchestratorInfoResponse orchestratorInfoResponse) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            log.warn("Кэш {} не найден! Сообщение будет обработано как первое.", CACHE_NAME);
            return true;
        }

        // создаём ключ по accountId + timestamp (LocalDate → Instant для уникальности)
        String key = buildKey(orchestratorInfoResponse.accountId(), orchestratorInfoResponse.timestamp());
        Boolean existed = cache.get(key, Boolean.class);

        if (Boolean.TRUE.equals(existed)) {
            log.info("Повторное сообщение (дубликат) для accountId={} timestamp={}", orchestratorInfoResponse.accountId(), orchestratorInfoResponse.timestamp());
            return false;
        }

        cache.put(key, true);
        log.info("Первое сообщение для accountId={} timestamp={} → сохраняем в кэш", orchestratorInfoResponse.accountId(), orchestratorInfoResponse.timestamp());
        return true;
    }

    private String buildKey(String accountId, LocalDate timestamp) {
        // Приводим LocalDate к Instant для единообразного ключа
        Instant instant = timestamp.atStartOfDay().toInstant(ZoneOffset.UTC);
        return accountId + "|" + instant.toEpochMilli();
    }
}
