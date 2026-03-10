package ru.vtb.msa.noma.orchestrator.cache.repositorycache;

import org.springframework.data.repository.CrudRepository;
import ru.vtb.msa.noma.orchestrator.cache.entitycache.InsuranceLifeCache;

import java.util.List;

/**
 * Spring Data Redis Repository для InsuranceLifeCache.
 *
 * Работает с Redis Hash-структурами через @RedisHash.
 * Поддерживает стандартные CRUD-операции + поиск по @Indexed полям.
 *
 * Примеры ключей в Redis:
 * - InsuranceLifeCache:{id}                          — основной Hash
 * - InsuranceLifeCache:{id}:idx                      — служебный set для индексов
 * - InsuranceLifeCache:clientId:{clientId}            — вторичный индекс
 * - InsuranceLifeCache:policyNumber:{policyNumber}    — вторичный индекс
 */
public interface InsuranceLifeCacheRepository extends CrudRepository<InsuranceLifeCache, String> {

    /**
     * Поиск всех кэшированных полисов по clientId.
     * Использует вторичный индекс @Indexed на поле clientId.
     */
    List<InsuranceLifeCache> findByClientId(String clientId);

    /**
     * Поиск кэшированного полиса по номеру полиса.
     * Использует вторичный индекс @Indexed на поле policyNumber.
     */
    List<InsuranceLifeCache> findByPolicyNumber(String policyNumber);
}
