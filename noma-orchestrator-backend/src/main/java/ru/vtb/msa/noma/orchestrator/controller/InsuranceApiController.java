package ru.vtb.msa.noma.orchestrator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.msa.noma.orchestrator.InsuranceApi;
import ru.vtb.msa.noma.orchestrator.annotation.Monitor;
import ru.vtb.msa.noma.orchestrator.enums.MetricName;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;
import ru.vtb.msa.noma.orchestrator.service.InsuranceLifeService;

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
}
