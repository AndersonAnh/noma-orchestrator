package ru.vtb.msa.noma.orchestrator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vtb.msa.noma.orchestrator.calculator.PremiumCalculator;
import ru.vtb.msa.noma.orchestrator.db.entity.InsuranceLife;
import ru.vtb.msa.noma.orchestrator.db.repository.InsuranceLifeRepository;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.client.ComplexCheckClient;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InsuranceLifeServiceTest {

    @Mock
    private InsuranceLifeRepository insuranceLifeRepository;
    @Mock
    private PremiumCalculator premiumCalculator;
    @Mock
    private DtoMapper dtoMapper;
    @Mock
    private ComplexCheckClient complexCheckClient;

    @InjectMocks
    private InsuranceLifeService insuranceLifeService;

    @Test
    void shouldCreateLifePolicyTest() {
        InsuranceLifeRequest request = TestUtilInsuranceLife.getValidRequest();
        ComplexCheckResponse response = TestUtilInsuranceLife.createAllowResponse();
        InsuranceLife insurance = TestUtilInsuranceLife.createInsurance();
        InsuranceLifePolicyResponse insuranceLifePolicyResponse = TestUtilInsuranceLife.createFullResponse();
        BigDecimal insuredAmount = BigDecimal.valueOf(1000);
        BigDecimal premium = BigDecimal.valueOf(1000);

        when(complexCheckClient.complexCheck(request)).thenReturn(response);
        when(premiumCalculator.calculateInsuredAmount(request.baseInsuredAmount(), request.insurancePeriod())).thenReturn(insuredAmount);
        when(premiumCalculator.calculatePremium(request.baseInsuredAmount(), request.insurancePeriod())).thenReturn(premium);
        when(dtoMapper.toInsurance(request, insuredAmount, premium)).thenReturn(insurance);
        when(insuranceLifeRepository.save(insurance)).thenReturn(insurance);
        when(dtoMapper.toInsuranceResponse(insurance)).thenReturn(insuranceLifePolicyResponse);

        InsuranceLifePolicyResponse result = insuranceLifeService.createLifePolicy(request);
        assertNotNull(result);
        assertEquals(insuranceLifePolicyResponse, result);

        verify(complexCheckClient).complexCheck(request);
        verify(premiumCalculator).calculateInsuredAmount(request.baseInsuredAmount(), request.insurancePeriod());
        verify(premiumCalculator).calculatePremium(request.baseInsuredAmount(), request.insurancePeriod());
        verify(dtoMapper).toInsurance(request, insuredAmount, premium);
        verify(insuranceLifeRepository).save(insurance);
        verify(dtoMapper).toInsuranceResponse(insurance);
    }
}
