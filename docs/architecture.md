# Architecture

## Runtime flow

```mermaid
sequenceDiagram
    actor User
    participant Web as Angular SPA
    participant IAM as Keycloak
    participant API as Spring Boot API
    participant ML as Python risk service
    participant DB as PostgreSQL

    User->>Web: Open quote workspace
    Web->>IAM: Authorization Code + PKCE
    IAM-->>Web: Signed access token
    User->>Web: Submit quote factors
    Web->>API: POST /api/quotes + Bearer token
    API->>IAM: Validate issuer and signing key
    API->>ML: POST /predict
    ML-->>API: Score, band, premium, factors
    API->>DB: Persist assessed quote
    API-->>Web: Created quote
```

## Boundaries

| Component | Responsibility | Failure behavior |
| --- | --- | --- |
| Angular SPA | Authenticated workflow and presentation | Shows an actionable service error |
| Keycloak | Identity, PKCE login, realm roles | Protected operations remain unavailable |
| Spring Boot API | Validation, authorization, orchestration, persistence | Persists a pending-review quote if ML is unavailable |
| Python risk service | Model training, prediction, explanation | Health endpoint reports model availability |
| PostgreSQL | Durable quote and identity data | Containers wait for database readiness |
| Prometheus | Scrapes bounded application and runtime metrics | Application traffic continues if monitoring is unavailable |
| Grafana | Provisions a read-only platform dashboard | Queries Prometheus without application credentials |

## ML design

The demo model is trained from deterministic synthetic data during the image build. This avoids redistributing personal or third-party insurance data. The model is intentionally educational: its output must not be used for real underwriting decisions. The response includes a bounded probability, a risk band, a recommended premium, and the three strongest model factors.

## Security model

- Public client: `insurflow-web`
- Flow: Authorization Code with PKCE (`S256`)
- Realm roles: `customer`, `admin`
- API: stateless OAuth 2.0 resource server
- CORS: restricted to the configured frontend origin
- Secrets: environment-only; local demo values are explicitly non-production

## Kubernetes operating model

The cluster topology keeps the browser, API, identity provider, and risk service behind a single ingress host. PostgreSQL is stateful; the application workloads are replicated and stateless. Startup, readiness, and liveness probes prevent premature routing and restart unhealthy containers. CPU-based autoscaling applies to the Spring Boot and risk services, while Pod Disruption Budgets preserve one available replica during voluntary maintenance.

Prometheus scrapes the internal service names and is not exposed by ingress. Grafana is also internal and can be accessed through an authenticated port-forward or placed behind an organization identity proxy. See the [operations guide](operations.md).
