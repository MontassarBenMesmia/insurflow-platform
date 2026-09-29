from pathlib import Path

import joblib
import numpy as np

from .schemas import RiskRequest, RiskResponse
from .train import train


FEATURE_LABELS = {
    "age": "Applicant age profile",
    "annual_income": "Income-to-coverage balance",
    "coverage_amount": "Requested coverage amount",
    "previous_claims": "Previous claims history",
    "vehicle_age": "Vehicle age",
}


class RiskModel:
    def __init__(self, artifact_path: Path):
        if not artifact_path.exists():
            train(artifact_path)
        artifact = joblib.load(artifact_path)
        self.pipeline = artifact["pipeline"]
        self.features = artifact["features"]

    def predict(self, request: RiskRequest) -> RiskResponse:
        row = np.array(
            [[
                request.age,
                request.annual_income,
                request.coverage_amount,
                request.previous_claims,
                request.vehicle_age,
            ]],
            dtype=float,
        )
        score = float(self.pipeline.predict_proba(row)[0, 1])
        if score < 0.35:
            band = "LOW"
        elif score < 0.68:
            band = "MEDIUM"
        else:
            band = "HIGH"

        base_rate = 0.008
        premium = request.coverage_amount * base_rate * (0.72 + score * 1.55)
        factors = self._explain(row)
        return RiskResponse(
            risk_score=round(score, 4),
            risk_band=band,
            recommended_premium=round(max(120.0, premium), 2),
            factors=factors,
        )

    def _explain(self, row: np.ndarray) -> list[str]:
        scaler = self.pipeline.named_steps["scaler"]
        classifier = self.pipeline.named_steps["classifier"]
        contributions = scaler.transform(row)[0] * classifier.coef_[0]
        ranked = np.argsort(np.abs(contributions))[::-1][:3]
        return [FEATURE_LABELS[self.features[index]] for index in ranked]
