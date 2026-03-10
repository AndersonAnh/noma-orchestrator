package ru.vtb.msa.noma.orchestrator.integration.infoservice.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import ru.vtb.msa.OrchestratorInfoResponse;

import java.time.Duration;
import java.time.LocalDate;

@Component
@Slf4j
@RequiredArgsConstructor
public class InfoSystemProcessor {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "noma:seenMessages:";
    private static final long TTL_HOURS = 24;

    public boolean firstTime(OrchestratorInfoResponse response) {
        String key = KEY_PREFIX + buildKey(response.accountId(), response.timestamp());

        Boolean wasAbsent = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofHours(TTL_HOURS));

        if (Boolean.TRUE.equals(wasAbsent)) {
            log.info("Первое сообщение для accountId={} timestamp={}",
                    response.accountId(), response.timestamp());
            return true;
        }

        log.info("Дубликат для accountId={} timestamp={}",
                response.accountId(), response.timestamp());
        return false;
    }

    private String buildKey(String accountId, LocalDate timestamp) {
        return accountId + "|" + timestamp;
    }
}