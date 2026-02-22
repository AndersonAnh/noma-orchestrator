package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vtb.msa.noma.orchestrator.calculator.PremiumCalculator;
import ru.vtb.msa.noma.orchestrator.db.entity.Client;
import ru.vtb.msa.noma.orchestrator.db.entity.InsuranceLife;
import ru.vtb.msa.noma.orchestrator.db.repository.ClientRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.InsuranceLifeRepository;
import ru.vtb.msa.noma.orchestrator.exception.ClientNotFoundException;
import ru.vtb.msa.noma.orchestrator.exception.IllegalCoverageType;
import ru.vtb.msa.noma.orchestrator.exception.NotValidAgeOfClientException;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.client.ComplexCheckClient;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.integration.risk.client.RiskClient;
import ru.vtb.msa.noma.orchestrator.integration.risk.enums.CoverageType;
import ru.vtb.msa.noma.orchestrator.integration.risk.pojo.RiskResponse;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferResponse;
import ru.vtb.msa.noma.orchestrator.utils.CheckResponseUtil;
import ru.vtb.msa.noma.orchestrator.utils.InsuranceOfferUtil;
import ru.vtb.msa.noma.orchestrator.utils.InsuranceRequestValidatorUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceLifeService {

    private final static List<CoverageType> COVERAGE_TYPES = List.of(CoverageType.TERM, CoverageType.WHOLE_LIFE, CoverageType.INVESTMENT);

    private final InsuranceLifeRepository insuranceLifeRepository;
    private final ClientRepository clientRepository;
    private final PremiumCalculator premiumCalculator;
    private final DtoMapper dtoMapper;
    private final ComplexCheckClient complexCheckClient;
    private final RiskClient riskClient;

    @Transactional
    public InsuranceLifePolicyResponse createLifePolicy(InsuranceLifeRequest request) {
        log.info("=== НАЧАЛО: Создание полиса страхования жизни ===");
        log.info("Входные параметры: baseInsuredAmount={}, insurancePeriod={} месяцев",
                request.baseInsuredAmount(), request.insurancePeriod());

        InsuranceRequestValidatorUtil.validate(request);
        log.debug("Валидация входных данных пройдена успешно");

        log.info("Вызов ComplexCheckClient для проверки клиента");
        ComplexCheckResponse checkResponse = complexCheckClient.complexCheck(request);
        log.info("Ответ от ComplexCheckClient: status={}", checkResponse);

        CheckResponseUtil.complexCheckResponseProcessing(checkResponse);
        log.debug("Обработка ответа ComplexCheck завершена");

        BigDecimal insuredAmount = premiumCalculator.calculateInsuredAmount(request.baseInsuredAmount(), request.insurancePeriod());
        BigDecimal premium = premiumCalculator.calculatePremium(request.baseInsuredAmount(), request.insurancePeriod());
        log.info("Расчет премии: insuredAmount={}, premium={}", insuredAmount, premium);

        InsuranceLife insurance = dtoMapper.toInsurance(request, insuredAmount, premium);

        insurance = insuranceLifeRepository.save(insurance);
        log.info("Полис сохранен в БД с ID: {}", insurance.getId());

        log.info("=== КОНЕЦ: Полис успешно создан ===");
        return dtoMapper.toInsuranceResponse(insurance);
    }

    public List<InsuranceOfferResponse> generateInsuranceOffers(InsuranceOfferRequest request) {
        log.info("=== НАЧАЛО: Создание предложений страхования ===");
        log.info("Входные параметры: clientId={}, age={}, coverageAmount={}, policyTermYears={}, coverageType={}",
                request.clientExternalId(), request.insuredAge(), request.desiredCoverageAmount(),
                request.policyTermYears(), request.coverageType());

        Client client = clientRepository.findById(request.clientExternalId())
                .orElseThrow(() -> {
                    log.warn("Клиент не найден в БД: {}", request.clientExternalId());
                    return new ClientNotFoundException("Клиент с указанным ID не найден");
                });
        log.debug("Клиент найден в БД: {}", request.clientExternalId());

        Integer actualAge = client.getAge();
        if (actualAge == null) {
            log.warn("Возраст клиента не установлен в БД. Используем возраст из запроса: {}", request.insuredAge());
            actualAge = request.insuredAge();
        } else {
            log.info("Используем реальный возраст клиента из БД: {} (запрос содержал: {})", actualAge, request.insuredAge());
        }

        if (actualAge < 18) {
            log.warn("Возраст клиента меньше 18 лет: {}", actualAge);
            throw new NotValidAgeOfClientException("Возраст клиента должен быть не менее 18 лет");
        }
        log.debug("Возраст клиента валиден: {} лет", actualAge);

        log.debug("Валидация типа покрытия: {}", request.coverageType());
        validateCoverageType(request.coverageType());
        log.debug("Тип покрытия валиден");

        InsuranceOfferRequest requestWithActualAge = InsuranceOfferRequest.builder()
                .clientExternalId(request.clientExternalId())
                .insuredAge(actualAge)
                .desiredCoverageAmount(request.desiredCoverageAmount())
                .policyTermYears(request.policyTermYears())
                .coverageType(request.coverageType())
                .isHighRiskOccupation(request.isHighRiskOccupation())
                .hasDangerousHobbies(request.hasDangerousHobbies())
                .beneficiaryRelation(request.beneficiaryRelation())
                .build();

        log.info("Вызов RiskClient для оценки риска. Параметры: age={}, hasDangerousHobbies={}, isHighRiskOccupation={}",
                actualAge, request.hasDangerousHobbies(), request.isHighRiskOccupation());
        RiskResponse riskResponse = riskClient.riskCheck(requestWithActualAge);
        BigDecimal riskFactor = BigDecimal.valueOf(riskResponse.riskFactor());
        log.info("Ответ от RiskClient: riskFactor={}, riskLevel={}", riskFactor, riskResponse.riskLevel());

        List<InsuranceOfferResponse> offers = new ArrayList<>();

        CoverageType primaryCoverageType = InsuranceOfferUtil.determinePrimaryCoverageType(request.coverageType());
        log.debug("Основной тип покрытия: {}", primaryCoverageType);

        log.debug("Создание основного предложения (тип: {})", primaryCoverageType);
        InsuranceOfferResponse primaryOffer = createOfferForCoverageType(
                primaryCoverageType,
                request.desiredCoverageAmount(),
                request.policyTermYears(),
                riskFactor
        );
        log.debug("Основное предложение создано: policyNumber={}, premium={}",
                primaryOffer.policyNumber(), primaryOffer.premiumAmount());
        offers.add(primaryOffer);

        CoverageType alternativeCoverageType1 = primaryCoverageType.getAlternativeCoverageType();
        log.debug("Создание альтернативного предложения 1 (тип: {})", alternativeCoverageType1);
        InsuranceOfferResponse alternativeOffer1 = createOfferForCoverageType(
                alternativeCoverageType1,
                request.desiredCoverageAmount(),
                request.policyTermYears(),
                riskFactor
        );
        log.debug("Альтернативное предложение 1 создано: policyNumber={}, premium={}",
                alternativeOffer1.policyNumber(), alternativeOffer1.premiumAmount());
        offers.add(alternativeOffer1);

        CoverageType alternativeCoverageType2 = alternativeCoverageType1.getAlternativeCoverageType();
        Integer adjustedTermYears = InsuranceOfferUtil.adjustPolicyTerm(request.policyTermYears());
        BigDecimal adjustedCoverageAmount = request.desiredCoverageAmount().multiply(new BigDecimal("0.8"));
        log.debug("Создание альтернативного предложения 2 (тип: {}, adjustedTerm: {}, adjustedAmount: {})",
                alternativeCoverageType2, adjustedTermYears, adjustedCoverageAmount);
        InsuranceOfferResponse alternativeOffer2 = createOfferForCoverageType(
                alternativeCoverageType2,
                adjustedCoverageAmount,
                adjustedTermYears,
                riskFactor
        );
        log.debug("Альтернативное предложение 2 создано: policyNumber={}, premium={}",
                alternativeOffer2.policyNumber(), alternativeOffer2.premiumAmount());
        offers.add(alternativeOffer2);

        log.info("=== КОНЕЦ: Успешно создано {} предложений страхования ===", offers.size());
        log.info("Сводка предложений: offer1(type={}, premium={}), offer2(type={}, premium={}), offer3(type={}, premium={})",
                primaryOffer.coverageType(), primaryOffer.premiumAmount(),
                alternativeOffer1.coverageType(), alternativeOffer1.premiumAmount(),
                alternativeOffer2.coverageType(), alternativeOffer2.premiumAmount());

        return offers;
    }

    private InsuranceOfferResponse createOfferForCoverageType(
            CoverageType coverageType,
            BigDecimal coverageAmount,
            Integer policyTermYears,
            BigDecimal riskFactor) {

        BigDecimal premiumAmount = premiumCalculator.calculateInsurancePremium(
                coverageAmount,
                policyTermYears,
                riskFactor,
                coverageType
        );

        String policyNumber = InsuranceOfferUtil.generatePolicyNumber();
        String description = InsuranceOfferUtil.generateDescription(coverageType, policyTermYears, riskFactor);

        return new InsuranceOfferResponse(
                policyNumber,
                coverageType.name(),
                coverageAmount,
                premiumAmount,
                policyTermYears,
                description,
                riskFactor
        );
    }

    private void validateCoverageType(String coverageType) {
        if (!COVERAGE_TYPES.contains(CoverageType.valueOf(coverageType.toUpperCase()))) {
            throw new IllegalCoverageType("Неподдерживаемый тип покрытия: " + coverageType);
        }
    }
}