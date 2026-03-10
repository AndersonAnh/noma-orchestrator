package ru.vtb.msa.noma.orchestrator.mapper;

import org.springframework.stereotype.Component;
import ru.vtb.msa.noma.orchestrator.db.entity.InsuranceLife;
import ru.vtb.msa.noma.orchestrator.cache.entitycache.InsuranceLifeCache;

/**
 * Маппер между JPA-сущностью InsuranceLife (PostgreSQL) и Redis-сущностью InsuranceLifeCache.
 *
 * Необходим потому что InsuranceLife содержит JPA-аннотации (@Entity, @Column и т.д.),
 * которые несовместимы с @RedisHash. Поэтому для Redis используется отдельная сущность.
 *
 * UUID → String конвертация: Redis @Id работает со String,
 * а JPA @Id в InsuranceLife — с UUID.
 */
@Component
public class InsuranceLifeCacheMapper {

    /**
     * JPA-сущность → Redis-сущность.
     * Вызывается после сохранения в PostgreSQL для записи в Redis-кэш.
     */
    public InsuranceLifeCache toCache(InsuranceLife entity) {
        if (entity == null) {
            return null;
        }

        return InsuranceLifeCache.builder()
                .id(entity.getId().toString())
                .policyNumber(entity.getPolicyNumber())
                .clientId(entity.getClientId().toString())
                .agentId(entity.getAgentId().toString())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .insuredAmount(entity.getInsuredAmount())
                .premiumAmount(entity.getPremiumAmount())
                .policyStatus(entity.getPolicyStatus())
                .coverageType(entity.getCoverageType())
                .specialConditions(entity.getSpecialConditions())
                .isDeleted(entity.getIsDeleted())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    /**
     * Redis-сущность → JPA-сущность.
     * Вызывается при чтении из Redis-кэша для возврата в сервисный слой.
     */
    public InsuranceLife toEntity(InsuranceLifeCache cache) {
        if (cache == null) {
            return null;
        }

        return InsuranceLife.builder()
                .id(java.util.UUID.fromString(cache.getId()))
                .policyNumber(cache.getPolicyNumber())
                .clientId(java.util.UUID.fromString(cache.getClientId()))
                .agentId(java.util.UUID.fromString(cache.getAgentId()))
                .startDate(cache.getStartDate())
                .endDate(cache.getEndDate())
                .insuredAmount(cache.getInsuredAmount())
                .premiumAmount(cache.getPremiumAmount())
                .policyStatus(cache.getPolicyStatus())
                .coverageType(cache.getCoverageType())
                .specialConditions(cache.getSpecialConditions())
                .isDeleted(cache.getIsDeleted())
                .createdAt(cache.getCreatedAt())
                .updatedAt(cache.getUpdatedAt())
                .createdBy(cache.getCreatedBy())
                .updatedBy(cache.getUpdatedBy())
                .build();
    }
}
