from pathlib import Path

import joblib
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler


FEATURES = [
    "age",
    "annual_income",
    "coverage_amount",
    "previous_claims",
    "vehicle_age",
]


def generate_training_data(samples: int = 4_000, seed: int = 42):
    """Generate deterministic demo data without redistributing personal data."""
    rng = np.random.default_rng(seed)
    age = rng.integers(18, 81, samples)
    annual_income = rng.lognormal(mean=10.7, sigma=0.55, size=samples).clip(12_000, 350_000)
    coverage_amount = rng.lognormal(mean=11.0, sigma=0.65, size=samples).clip(5_000, 750_000)
    previous_claims = rng.poisson(0.7, samples).clip(0, 8)
    vehicle_age = rng.integers(0, 26, samples)

    matrix = np.column_stack([age, annual_income, coverage_amount, previous_claims, vehicle_age])
    coverage_ratio = coverage_amount / annual_income
    logit = (
        -2.4
        + previous_claims * 0.78
        + vehicle_age * 0.055
        + coverage_ratio * 0.28
        + np.abs(age - 45) * 0.015
        + rng.normal(0, 0.45, samples)
    )
    probability = 1 / (1 + np.exp(-logit))
    labels = rng.binomial(1, probability)
    return matrix, labels


def train(output_path: Path) -> None:
    features, labels = generate_training_data()
    pipeline = Pipeline(
        [
            ("scaler", StandardScaler()),
            ("classifier", LogisticRegression(max_iter=1_000, random_state=42)),
        ]
    )
    pipeline.fit(features, labels)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    joblib.dump({"pipeline": pipeline, "features": FEATURES, "version": "1.0.0"}, output_path)


if __name__ == "__main__":
    train(Path(__file__).resolve().parents[1] / "artifacts" / "risk_model.joblib")
