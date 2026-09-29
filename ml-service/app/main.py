from pathlib import Path

from fastapi import FastAPI

from .model import RiskModel
from .schemas import HealthResponse, RiskRequest, RiskResponse


MODEL_PATH = Path(__file__).resolve().parents[1] / "artifacts" / "risk_model.joblib"
model = RiskModel(MODEL_PATH)

app = FastAPI(
    title="InsurFlow Risk API",
    version="1.0.0",
    description="Explainable risk scoring for insurance quote demonstrations.",
)


@app.get("/health", response_model=HealthResponse, tags=["operations"])
def health() -> HealthResponse:
    return HealthResponse(status="ok", model_loaded=model is not None)


@app.post("/predict", response_model=RiskResponse, tags=["risk"])
def predict(request: RiskRequest) -> RiskResponse:
    return model.predict(request)
