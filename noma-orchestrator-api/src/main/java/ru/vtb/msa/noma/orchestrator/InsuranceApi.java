package ru.vtb.msa.noma.orchestrator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferResponse;

import java.util.List;

@RequestMapping("/api/v1/insurance")
public interface InsuranceApi {

    @PostMapping("/create-policy")
    ResponseEntity<InsuranceLifePolicyResponse> createLifePolicy(
            @RequestBody InsuranceLifeRequest request
    );

    @PostMapping("/offers")
    ResponseEntity<List<InsuranceOfferResponse>> generateInsuranceOffers(
            @RequestBody InsuranceOfferRequest request
    );
}