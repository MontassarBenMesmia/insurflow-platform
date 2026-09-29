package com.montassar.insurflow.risk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HttpRiskEngineClient implements RiskEngineClient {
    private final RestClient restClient;

    public HttpRiskEngineClient(RestClient.Builder builder, @Value("${app.risk-service.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public RiskAssessmentResponse assess(RiskAssessmentRequest request) {
        return restClient.post()
                .uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(RiskAssessmentResponse.class);
    }
}
