CREATE TABLE quotes (
    id UUID PRIMARY KEY,
    customer_name VARCHAR(120) NOT NULL,
    age INTEGER NOT NULL,
    annual_income NUMERIC(14, 2) NOT NULL,
    coverage_amount NUMERIC(14, 2) NOT NULL,
    previous_claims INTEGER NOT NULL,
    vehicle_age INTEGER NOT NULL,
    quote_type VARCHAR(20) NOT NULL,
    status VARCHAR(24) NOT NULL,
    risk_score DOUBLE PRECISION,
    risk_band VARCHAR(20),
    recommended_premium NUMERIC(12, 2),
    created_by VARCHAR(120) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_quotes_created_at ON quotes (created_at DESC);
