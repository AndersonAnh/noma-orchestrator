package ru.vtb.msa.noma.orchestrator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferResponse;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/insurance")
public interface InsuranceApi {

    @PostMapping("/create-policy")
    ResponseEntity<InsuranceLifePolicyResponse> createLifePolicy(
            @RequestBody InsuranceLifeRequest request
    );

    @GetMapping("/policy/{id}")
    ResponseEntity<InsuranceLifePolicyResponse> getLifePolicyById(
            @PathVariable UUID id
    );

    @DeleteMapping("/policy/{id}")
    ResponseEntity<Void> deleteLifePolicy(
            @PathVariable UUID id
    );

    @PostMapping("/offers")
    ResponseEntity<List<InsuranceOfferResponse>> generateInsuranceOffers(
            @RequestBody InsuranceOfferRequest request
    );
}