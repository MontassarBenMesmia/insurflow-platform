package com.montassar.insurflow.risk;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RiskAssessmentRequest(
        int age,
        @JsonProperty("annual_income") BigDecimal annualIncome,
        @JsonProperty("coverage_amount") BigDecimal coverageAmount,
        @JsonProperty("previous_claims") int previousClaims,
        @JsonProperty("vehicle_age") int vehicleAge) {
}
