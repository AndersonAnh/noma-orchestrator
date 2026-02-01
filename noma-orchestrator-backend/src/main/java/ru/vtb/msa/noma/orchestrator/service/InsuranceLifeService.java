package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vtb.msa.noma.orchestrator.calculator.PremiumCalculator;
import ru.vtb.msa.noma.orchestrator.db.entity.InsuranceLife;
import ru.vtb.msa.noma.orchestrator.db.repository.InsuranceLifeRepository;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.client.ComplexCheckClient;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;
import ru.vtb.msa.noma.orchestrator.utils.CheckResponseUtil;
import ru.vtb.msa.noma.orchestrator.utils.InsuranceRequestValidatorUtil;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceLifeService {
    private final InsuranceLifeRepository insuranceLifeRepository;
    private final PremiumCalculator premiumCalculator;
    private final DtoMapper dtoMapper;
    private final ComplexCheckClient complexCheckClient;

    @Transactional
    public InsuranceLifePolicyResponse createLifePolicy(InsuranceLifeRequest request) {
        InsuranceRequestValidatorUtil.validate(request);

        ComplexCheckResponse checkResponse = complexCheckClient.complexCheck(request);
        CheckResponseUtil.complexCheckResponseProcessing(checkResponse);

        BigDecimal insuredAmount = premiumCalculator.calculateInsuredAmount(request.baseInsuredAmount(), request.insurancePeriod());
        BigDecimal premium = premiumCalculator.calculatePremium(request.baseInsuredAmount(), request.insurancePeriod());

        InsuranceLife insurance = dtoMapper.toInsurance(request, insuredAmount, premium);

        insurance = insuranceLifeRepository.save(insurance);

        return dtoMapper.toInsuranceResponse(insurance);
    }
}