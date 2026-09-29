package com.montassar.insurflow.quote;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record QuoteResponse(
        UUID id,
        String customerName,
        int age,
        BigDecimal annualIncome,
        BigDecimal coverageAmount,
        int previousClaims,
        int vehicleAge,
        QuoteType quoteType,
        QuoteStatus status,
        Double riskScore,
        RiskBand riskBand,
        BigDecimal recommendedPremium,
        String createdBy,
        Instant createdAt) {

    static QuoteResponse from(Quote quote) {
        return new QuoteResponse(
                quote.getId(), quote.getCustomerName(), quote.getAge(), quote.getAnnualIncome(),
                quote.getCoverageAmount(), quote.getPreviousClaims(), quote.getVehicleAge(),
                quote.getQuoteType(), quote.getStatus(), quote.getRiskScore(), quote.getRiskBand(),
                quote.getRecommendedPremium(), quote.getCreatedBy(), quote.getCreatedAt());
    }
}
