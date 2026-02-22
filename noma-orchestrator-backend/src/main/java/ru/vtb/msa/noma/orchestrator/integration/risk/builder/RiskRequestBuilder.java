package ru.vtb.msa.noma.orchestrator.integration.risk.builder;

import org.springframework.stereotype.Component;
import ru.vtb.msa.noma.orchestrator.integration.risk.pojo.RiskRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferRequest;

/**
 * Построитель запроса для оценки риска страхового полиса.
 * <p>
 * Преобразует {@link InsuranceOfferRequest} в {@link RiskRequest}, подготавливая
 * все необходимые параметры для отправки в риск-сервис. Обеспечивает маппинг данных
 * между слоем API и интеграционным слоем.
 * </p>
 * <p>
 * Компонент используется в процессе создания страховых предложений для получения
 * коэффициента риска, который затем применяется при расчете премии.
 * </p>
 *
 * <h3>Поток данных:</h3>
 * <pre>
 * InsuranceOfferRequest
 *     ↓
 * RiskRequestBuilder.buildRiskRequest()
 *     ↓
 * RiskRequest
 *     ↓
 * RiskClient.riskCheck()
 *     ↓
 * RiskResponse (с коэффициентом риска)
 * </pre>
 *
 * <h3>Пример использования:</h3>
 * <pre>
 * {@code
 * InsuranceOfferRequest offerRequest = InsuranceOfferRequest.builder()
 *     .clientExternalId(UUID.randomUUID())
 *     .insuredAge(35)
 *     .desiredCoverageAmount(new BigDecimal("5000000"))
 *     .policyTermYears(10)
 *     .coverageType("WHOLE_LIFE")
 *     .hasDangerousHobbies(false)
 *     .isHighRiskOccupation(false)
 *     .beneficiaryRelation("Супруга")
 *     .build();
 *
 * RiskRequest riskRequest = riskRequestBuilder.buildRiskRequest(offerRequest);
 * RiskResponse response = riskClient.riskCheck(riskRequest);
 * }
 * </pre>
 *
 * @author Orchestrator Team
 * @version 1.0
 * @see RiskRequest
 * @see InsuranceOfferRequest
 * @see ru.vtb.msa.noma.orchestrator.integration.risk.client.RiskClient
 */
@Component
public class RiskRequestBuilder {

    /**
     * Преобразует запрос на страховое предложение в запрос для оценки риска.
     * <p>
     * Метод выполняет маппинг всех параметров из {@link InsuranceOfferRequest} в {@link RiskRequest},
     * обеспечивая полный набор данных для комплексного анализа риска риск-сервисом.
     * </p>
     *
     * <h3>Маппинг параметров:</h3>
     * <ul>
     *   <li>{@code clientExternalId} → {@code clientId} - идентификатор клиента для отслеживания истории</li>
     *   <li>{@code insuredAge} → {@code age} - возраст застрахованного</li>
     *   <li>{@code hasDangerousHobbies} → {@code hasDangerousHobbies} - наличие опасных хобби</li>
     *   <li>{@code isHighRiskOccupation} → {@code isHighRiskOccupation} - высокорисковая профессия</li>
     *   <li>{@code desiredCoverageAmount} → {@code desiredCoverageAmount} - сумма покрытия</li>
     *   <li>{@code policyTermYears} → {@code policyTermYears} - срок полиса</li>
     *   <li>{@code coverageType} → {@code coverageType} - тип покрытия (TERM, WHOLE_LIFE, INVESTMENT)</li>
     *   <li>{@code beneficiaryRelation} → {@code beneficiaryRelation} - отношение бенефициара</li>
     * </ul>
     *
     * @param request запрос на страховое предложение, содержащий все необходимые параметры.
     *                Не должен быть null.
     *
     * @return построенный {@link RiskRequest} со всеми параметрами, готовый для отправки
     *         в риск-сервис. Никогда не возвращает null.
     *
     * @throws NullPointerException если {@code request} равен null
     *
     * @see RiskRequest
     * @see InsuranceOfferRequest
     */
    public RiskRequest buildRiskRequest(InsuranceOfferRequest request) {

        return RiskRequest.builder()
                .clientId(request.clientExternalId())
                .age(request.insuredAge())
                .hasDangerousHobbies(request.hasDangerousHobbies())
                .isHighRiskOccupation(request.isHighRiskOccupation())
                .desiredCoverageAmount(request.desiredCoverageAmount())
                .policyTermYears(request.policyTermYears())
                .coverageType(request.coverageType())
                .beneficiaryRelation(request.beneficiaryRelation())
                .build();
    }
}
