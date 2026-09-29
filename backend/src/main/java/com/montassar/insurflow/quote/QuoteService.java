package com.montassar.insurflow.quote;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import com.montassar.insurflow.risk.RiskAssessmentRequest;
import com.montassar.insurflow.risk.RiskAssessmentResponse;
import com.montassar.insurflow.risk.RiskEngineClient;

@Service
public class QuoteService {
    private static final Logger log = LoggerFactory.getLogger(QuoteService.class);
    private final QuoteRepository quoteRepository;
    private final RiskEngineClient riskEngineClient;

    public QuoteService(QuoteRepository quoteRepository, RiskEngineClient riskEngineClient) {
        this.quoteRepository = quoteRepository;
        this.riskEngineClient = riskEngineClient;
    }

    @Transactional
    public QuoteResponse create(CreateQuoteRequest request, String username) {
        Quote quote = new Quote(
                request.customerName(), request.age(), request.annualIncome(), request.coverageAmount(),
                request.previousClaims(), request.vehicleAge(), request.quoteType(), username);

        try {
            RiskAssessmentResponse assessment = riskEngineClient.assess(new RiskAssessmentRequest(
                    request.age(), request.annualIncome(), request.coverageAmount(),
                    request.previousClaims(), request.vehicleAge()));
            if (assessment != null) {
                quote.applyAssessment(
                        assessment.riskScore(),
                        RiskBand.valueOf(assessment.riskBand().toUpperCase()),
                        assessment.recommendedPremium());
            }
        } catch (RestClientException | IllegalArgumentException exception) {
            // Persist a reviewable quote even when the ML service is temporarily unavailable.
            log.warn("Risk assessment unavailable; quote will be marked for review: {}", exception.getMessage());
        }

        return QuoteResponse.from(quoteRepository.save(quote));
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> findAll() {
        return quoteRepository.findAllByOrderByCreatedAtDesc().stream().map(QuoteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public QuoteResponse findById(UUID id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new QuoteNotFoundException(id));
        return QuoteResponse.from(quote);
    }
}
