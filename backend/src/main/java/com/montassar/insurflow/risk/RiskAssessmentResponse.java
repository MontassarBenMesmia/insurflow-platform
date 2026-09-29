package com.montassar.insurflow.risk;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RiskAssessmentResponse(
        @JsonProperty("risk_score") double riskScore,
        @JsonProperty("risk_band") String riskBand,
        @JsonProperty("recommended_premium") BigDecimal recommendedPremium,
        List<String> factors) {
}
