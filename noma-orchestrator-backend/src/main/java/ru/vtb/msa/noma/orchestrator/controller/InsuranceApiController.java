package ru.vtb.msa.noma.orchestrator.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.msa.noma.orchestrator.InsuranceApi;
import ru.vtb.msa.noma.orchestrator.annotation.Monitor;
import ru.vtb.msa.noma.orchestrator.enums.MetricName;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferResponse;
import ru.vtb.msa.noma.orchestrator.service.InsuranceLifeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InsuranceApiController implements InsuranceApi {

    private final InsuranceLifeService insuranceLifeService;

    @Monitor(metricName = MetricName.CREATE_LIFE_POLICY)
    @Override
    public ResponseEntity<InsuranceLifePolicyResponse> createLifePolicy(@RequestBody InsuranceLifeRequest request) {
        InsuranceLifePolicyResponse response = insuranceLifeService.createLifePolicy(request);
        return ResponseEntity.ok(response);
    }

    @Monitor(metricName = MetricName.CREATE_INSURANCE_OFFER)
    @Override
    public ResponseEntity<List<InsuranceOfferResponse>> generateInsuranceOffers(@Valid @RequestBody InsuranceOfferRequest request) {
        List<InsuranceOfferResponse> offers = insuranceLifeService.generateInsuranceOffers(request);
        return ResponseEntity.ok(offers);
    }
}
