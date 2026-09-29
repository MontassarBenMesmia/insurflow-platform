from pydantic import BaseModel, Field


class RiskRequest(BaseModel):
    age: int = Field(ge=18, le=100)
    annual_income: float = Field(gt=0, le=10_000_000)
    coverage_amount: float = Field(ge=1_000, le=20_000_000)
    previous_claims: int = Field(ge=0, le=20)
    vehicle_age: int = Field(ge=0, le=50)


class RiskResponse(BaseModel):
    risk_score: float
    risk_band: str
    recommended_premium: float
    factors: list[str]


class HealthResponse(BaseModel):
    status: str
    model_loaded: bool
