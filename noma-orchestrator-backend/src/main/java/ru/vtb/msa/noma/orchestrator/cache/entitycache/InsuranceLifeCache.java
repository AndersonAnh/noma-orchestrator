package ru.vtb.msa.noma.orchestrator.cache.entitycache;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import ru.vtb.msa.noma.orchestrator.enums.CoverageType;
import ru.vtb.msa.noma.orchestrator.enums.PolicyStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Redis-сущность для кэширования полисов страхования жизни.
 * Хранится в Redis как Hash-структура с TTL 30 минут (1800 секунд).
 *
 * Ключ в Redis: InsuranceLifeCache:{id}
 * Пример: InsuranceLifeCache:550e8400-e29b-41d4-a716-446655440000
 *
 * @Indexed поля создают вторичные индексы в Redis,
 * позволяя искать по clientId и policyNumber без полного сканирования.
 */
@RedisHash(value = "InsuranceLifeCache", timeToLive = 1800)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InsuranceLifeCache implements Serializable {

    @Id
    private String id;

    @Indexed
    private String policyNumber;

    @Indexed
    private String clientId;

    private String agentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal insuredAmount;
    private BigDecimal premiumAmount;
    private PolicyStatus policyStatus;
    private CoverageType coverageType;
    private String specialConditions;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
