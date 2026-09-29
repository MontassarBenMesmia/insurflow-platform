package com.montassar.insurflow.quote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.montassar.insurflow.risk.RiskAssessmentResponse;
import com.montassar.insurflow.risk.RiskEngineClient;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {
    @Mock
    private QuoteRepository repository;
    @Mock
    private RiskEngineClient riskEngineClient;

    private QuoteService service;

    @BeforeEach
    void setUp() {
        service = new QuoteService(repository, riskEngineClient);
        when(repository.save(any(Quote.class))).thenAnswer(invocation -> {
            Quote quote = invocation.getArgument(0);
            ReflectionTestUtils.setField(quote, "id", UUID.randomUUID());
            return quote;
        });
    }

    @Test
    void createsAnAssessedQuote() {
        when(riskEngineClient.assess(any())).thenReturn(new RiskAssessmentResponse(
                0.72, "HIGH", new BigDecimal("1840.00"), java.util.List.of("Previous claims")));

        QuoteResponse result = service.create(new CreateQuoteRequest(
                "Demo Customer", 42, new BigDecimal("52000"), new BigDecimal("95000"),
                2, 8, QuoteType.AUTO), "demo");

        assertThat(result.id()).isNotNull();
        assertThat(result.status()).isEqualTo(QuoteStatus.ASSESSED);
        assertThat(result.riskBand()).isEqualTo(RiskBand.HIGH);
        assertThat(result.recommendedPremium()).isEqualByComparingTo("1840.00");
    }
}
