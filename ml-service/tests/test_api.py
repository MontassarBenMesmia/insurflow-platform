from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_health_reports_loaded_model():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok", "model_loaded": True}


def test_metrics_expose_request_and_latency_series():
    client.get("/health")
    response = client.get("/metrics")
    assert response.status_code == 200
    assert "risk_api_requests_total" in response.text
    assert "risk_api_request_duration_seconds" in response.text


def test_prediction_is_explainable_and_bounded():
    response = client.post(
        "/predict",
        json={
            "age": 41,
            "annual_income": 58_000,
            "coverage_amount": 90_000,
            "previous_claims": 2,
            "vehicle_age": 9,
        },
    )
    assert response.status_code == 200
    body = response.json()
    assert 0 <= body["risk_score"] <= 1
    assert body["risk_band"] in {"LOW", "MEDIUM", "HIGH"}
    assert body["recommended_premium"] > 0
    assert len(body["factors"]) == 3


def test_invalid_payload_is_rejected():
    response = client.post(
        "/predict",
        json={
            "age": 12,
            "annual_income": 0,
            "coverage_amount": 100,
            "previous_claims": -1,
            "vehicle_age": 70,
        },
    )
    assert response.status_code == 422
