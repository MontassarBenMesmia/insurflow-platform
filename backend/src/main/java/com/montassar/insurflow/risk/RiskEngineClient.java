package com.montassar.insurflow.risk;

public interface RiskEngineClient {
    RiskAssessmentResponse assess(RiskAssessmentRequest request);
}
