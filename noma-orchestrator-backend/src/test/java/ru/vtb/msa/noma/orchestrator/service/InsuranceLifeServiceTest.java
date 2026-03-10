package ru.vtb.msa.noma.orchestrator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vtb.msa.noma.orchestrator.calculator.PremiumCalculator;
import ru.vtb.msa.noma.orchestrator.db.entity.Client;
import ru.vtb.msa.noma.orchestrator.db.entity.InsuranceLife;
import ru.vtb.msa.noma.orchestrator.cache.entitycache.InsuranceLifeCache;
import ru.vtb.msa.noma.orchestrator.db.repository.ClientRepository;
import ru.vtb.msa.noma.orchestrator.cache.repositorycache.InsuranceLifeCacheRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.InsuranceLifeRepository;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.client.ComplexCheckClient;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.integration.risk.client.RiskClient;
import ru.vtb.msa.noma.orchestrator.integration.risk.enums.CoverageType;
import ru.vtb.msa.noma.orchestrator.integration.risk.pojo.RiskResponse;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.mapper.InsuranceLifeCacheMapper;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InsuranceLifeServiceTest {

    @Mock
    private InsuranceLifeRepository insuranceLifeRepository;
    @Mock
    private InsuranceLifeCacheRepository cacheRepository;
    @Mock
    private InsuranceLifeCacheMapper cacheMapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PremiumCalculator premiumCalculator;
    @Mock
    private DtoMapper dtoMapper;
    @Mock
    private ComplexCheckClient complexCheckClient;
    @Mock
    private RiskClient riskClient;

    @InjectMocks
    private InsuranceLifeService insuranceLifeService;

    @Test
    void shouldCreateLifePolicyTest() {
        InsuranceLifeRequest request = TestUtilInsuranceLife.getValidRequest();
        ComplexCheckResponse response = TestUtilInsuranceLife.createAllowResponse();
        InsuranceLife insurance = TestUtilInsuranceLife.createInsurance();
        InsuranceLifePolicyResponse insuranceLifePolicyResponse = TestUtilInsuranceLife.createFullResponse();
        InsuranceLifeCache cacheEntity = new InsuranceLifeCache();
        BigDecimal insuredAmount = BigDecimal.valueOf(1000);
        BigDecimal premium = BigDecimal.valueOf(1000);

        when(complexCheckClient.complexCheck(request)).thenReturn(response);
        when(premiumCalculator.calculateInsuredAmount(request.baseInsuredAmount(), request.insurancePeriod())).thenReturn(insuredAmount);
        when(premiumCalculator.calculatePremium(request.baseInsuredAmount(), request.insurancePeriod())).thenReturn(premium);
        when(dtoMapper.toInsurance(request, insuredAmount, premium)).thenReturn(insurance);
        when(insuranceLifeRepository.save(insurance)).thenReturn(insurance);
        when(cacheMapper.toCache(insurance)).thenReturn(cacheEntity);
        when(dtoMapper.toInsuranceResponse(insurance)).thenReturn(insuranceLifePolicyResponse);

        InsuranceLifePolicyResponse result = insuranceLifeService.createLifePolicy(request);
        assertNotNull(result);
        assertEquals(insuranceLifePolicyResponse, result);

        verify(complexCheckClient).complexCheck(request);
        verify(premiumCalculator).calculateInsuredAmount(request.baseInsuredAmount(), request.insurancePeriod());
        verify(premiumCalculator).calculatePremium(request.baseInsuredAmount(), request.insurancePeriod());
        verify(dtoMapper).toInsurance(request, insuredAmount, premium);
        verify(insuranceLifeRepository).save(insurance);
        verify(cacheMapper).toCache(insurance);
        verify(cacheRepository).save(cacheEntity);
        verify(dtoMapper).toInsuranceResponse(insurance);
    }

    @Test
    void shouldGenerateInsuranceOffersTest() {
        InsuranceOfferRequest request = TestUtilInsuranceLife.getValidOfferRequest();
        RiskResponse riskResponse = TestUtilInsuranceLife.getRiskResponse();

        Client mockClient = new Client();
        mockClient.setId(request.clientExternalId());
        mockClient.setAge(35);  // Возраст >= 18, чтобы пройти валидацию

        when(clientRepository.findById(request.clientExternalId()))
                .thenReturn(Optional.of(mockClient));

        when(riskClient.riskCheck(any(InsuranceOfferRequest.class)))
                .thenReturn(riskResponse);

        BigDecimal mockPremium = BigDecimal.valueOf(5000);
        when(premiumCalculator.calculateInsurancePremium(
                any(BigDecimal.class),
                any(Integer.class),
                any(BigDecimal.class),
                any(CoverageType.class)
        )).thenReturn(mockPremium);

        List<InsuranceOfferResponse> result = insuranceLifeService.generateInsuranceOffers(request);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertTrue(result.stream().anyMatch(offer -> "WHOLE_LIFE".equals(offer.coverageType())));
        assertTrue(result.stream().anyMatch(offer -> "TERM".equals(offer.coverageType())));
        assertTrue(result.stream().anyMatch(offer -> "INVESTMENT".equals(offer.coverageType())));

        verify(clientRepository).findById(request.clientExternalId());
        verify(riskClient).riskCheck(any(InsuranceOfferRequest.class));
        verify(premiumCalculator, org.mockito.Mockito.times(3)).calculateInsurancePremium(
                any(BigDecimal.class),
                any(Integer.class),
                any(BigDecimal.class),
                any(CoverageType.class)
        );
    }
}