package com.montassar.insurflow.quote;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "quotes")
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String customerName;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal annualIncome;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal coverageAmount;

    @Column(nullable = false)
    private int previousClaims;

    @Column(nullable = false)
    private int vehicleAge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuoteType quoteType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private QuoteStatus status;

    private Double riskScore;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RiskBand riskBand;

    @Column(precision = 12, scale = 2)
    private BigDecimal recommendedPremium;

    @Column(nullable = false, length = 120)
    private String createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Quote() {
    }

    public Quote(String customerName, int age, BigDecimal annualIncome, BigDecimal coverageAmount,
            int previousClaims, int vehicleAge, QuoteType quoteType, String createdBy) {
        this.customerName = customerName;
        this.age = age;
        this.annualIncome = annualIncome;
        this.coverageAmount = coverageAmount;
        this.previousClaims = previousClaims;
        this.vehicleAge = vehicleAge;
        this.quoteType = quoteType;
        this.createdBy = createdBy;
        this.status = QuoteStatus.PENDING_REVIEW;
        this.createdAt = Instant.now();
    }

    public void applyAssessment(double riskScore, RiskBand riskBand, BigDecimal recommendedPremium) {
        this.riskScore = riskScore;
        this.riskBand = riskBand;
        this.recommendedPremium = recommendedPremium;
        this.status = QuoteStatus.ASSESSED;
    }

    public UUID getId() { return id; }
    public String getCustomerName() { return customerName; }
    public int getAge() { return age; }
    public BigDecimal getAnnualIncome() { return annualIncome; }
    public BigDecimal getCoverageAmount() { return coverageAmount; }
    public int getPreviousClaims() { return previousClaims; }
    public int getVehicleAge() { return vehicleAge; }
    public QuoteType getQuoteType() { return quoteType; }
    public QuoteStatus getStatus() { return status; }
    public Double getRiskScore() { return riskScore; }
    public RiskBand getRiskBand() { return riskBand; }
    public BigDecimal getRecommendedPremium() { return recommendedPremium; }
    public String getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
}
