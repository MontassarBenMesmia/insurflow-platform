from pathlib import Path
from time import perf_counter

from fastapi import FastAPI, Request, Response
from prometheus_client import CONTENT_TYPE_LATEST, Counter, Histogram, generate_latest

from .model import RiskModel
from .schemas import HealthResponse, RiskRequest, RiskResponse


MODEL_PATH = Path(__file__).resolve().parents[1] / "artifacts" / "risk_model.joblib"
model = RiskModel(MODEL_PATH)
REQUESTS = Counter(
    "risk_api_requests_total",
    "Risk API requests grouped by route, method, and status.",
    ("route", "method", "status"),
)
REQUEST_DURATION = Histogram(
    "risk_api_request_duration_seconds",
    "Risk API request duration grouped by route and method.",
    ("route", "method"),
)

app = FastAPI(
    title="InsurFlow Risk API",
    version="1.0.0",
    description="Explainable risk scoring for insurance quote demonstrations.",
)


@app.middleware("http")
async def observe_requests(request: Request, call_next):
    started = perf_counter()
    response = await call_next(request)
    route = request.scope.get("route")
    route_path = getattr(route, "path", "unmatched")
    REQUESTS.labels(route_path, request.method, str(response.status_code)).inc()
    REQUEST_DURATION.labels(route_path, request.method).observe(perf_counter() - started)
    return response


@app.get("/metrics", include_in_schema=False)
def metrics() -> Response:
    return Response(content=generate_latest(), media_type=CONTENT_TYPE_LATEST)


@app.get("/health", response_model=HealthResponse, tags=["operations"])
def health() -> HealthResponse:
    return HealthResponse(status="ok", model_loaded=model is not None)


@app.post("/predict", response_model=RiskResponse, tags=["risk"])
def predict(request: RiskRequest) -> RiskResponse:
    return model.predict(request)
