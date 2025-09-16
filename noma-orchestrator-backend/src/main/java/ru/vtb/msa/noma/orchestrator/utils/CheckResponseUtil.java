package ru.vtb.msa.noma.orchestrator.utils;

import lombok.experimental.UtilityClass;
import ru.vtb.msa.noma.orchestrator.exception.*;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.enums.ComplexCheckResult;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudResponse;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudResult;

@UtilityClass
public class CheckResponseUtil {

    public void complexCheckResponseProcessing(ComplexCheckResponse response) {
        var decision = response.requestResult().getDecision();
        if (decision == ComplexCheckResult.DENY) {
            throw new ComplexCheckDenyException();
        }
        if (decision == ComplexCheckResult.ARBITRATION) {
            throw new ComplexCheckArbitrationException();
        }
    }

    public void handleFraudResponse(FraudResponse fraudResponse) {

        for (FraudResult result : fraudResponse.results()) {
            switch (result.fraudCheckStatus()) {
                case FRAUD_CONFIRMED -> throw new FraudDetectedException(
                        "Fraud detected for account " + result.accountId() + ": " + result.comment()
                );
                case REVIEW_REQUIRED -> throw new FraudReviewRequiredException(
                        "Manual review required: " + result.comment()
                );
                case ERROR -> throw new FraudServiceException(
                        "Error during fraud check: " + result.comment()
                );
                case SAFE, PENDING -> {
                }
            }
        }
    }
}
