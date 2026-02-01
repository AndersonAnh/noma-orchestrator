package ru.vtb.msa.noma.orchestrator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;

@RequestMapping("/api/insurance")
public interface InsuranceApi {

    @PostMapping("/create-policy")
    ResponseEntity<InsuranceLifePolicyResponse> createLifePolicy(
            @RequestBody InsuranceLifeRequest request
    );


}