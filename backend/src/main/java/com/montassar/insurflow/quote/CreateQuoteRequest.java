package com.montassar.insurflow.quote;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateQuoteRequest(
        @NotBlank @Size(max = 120) String customerName,
        @Min(18) @Max(100) int age,
        @NotNull @DecimalMin("1.00") BigDecimal annualIncome,
        @NotNull @DecimalMin("1000.00") BigDecimal coverageAmount,
        @Min(0) @Max(20) int previousClaims,
        @Min(0) @Max(50) int vehicleAge,
        @NotNull QuoteType quoteType) {
}
