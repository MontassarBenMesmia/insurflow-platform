# Operations guide

InsurFlow provides two operating targets: Docker Compose for a reproducible workstation environment and Kubernetes manifests for a production-style cluster demonstration. Both use the same application images and observability configuration.

## Local stack

Start the application services:

```bash
docker compose up --build
```

Add Prometheus and Grafana:

```bash
docker compose --profile observability up --build
```

| Service | Address |
| --- | --- |
| Application | http://localhost:4200 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

Grafana provisions the Prometheus data source and the **InsurFlow Platform Health** dashboard automatically. The local administrator defaults to `admin` / `change-me-locally`; override `GRAFANA_ADMIN_USER` and `GRAFANA_ADMIN_PASSWORD` in `.env`.

Application metrics are available internally at:

- Spring Boot: `http://localhost:8080/actuator/prometheus`
- Risk service: `http://localhost:8000/metrics`

## Kubernetes

The manifests target Kubernetes 1.31+ and use Kustomize. A complete demonstration cluster also needs:

- an Nginx Ingress controller;
- Metrics Server for Horizontal Pod Autoscalers;
- a default `ReadWriteOnce` storage class;
- at least 6 GB of available memory.

Container images are published to GitHub Container Registry by `.github/workflows/images.yml`.

### 1. Create the namespace and secrets

```bash
kubectl apply -f infra/kubernetes/namespace.yaml
kubectl create secret generic insurflow-secrets \
  --namespace insurflow \
  --from-literal=POSTGRES_USER=insurflow \
  --from-literal=POSTGRES_PASSWORD='<random-password>' \
  --from-literal=KEYCLOAK_ADMIN=admin \
  --from-literal=KEYCLOAK_ADMIN_PASSWORD='<different-random-password>' \
  --from-literal=GRAFANA_ADMIN_USER=admin \
  --from-literal=GRAFANA_ADMIN_PASSWORD='<third-random-password>'
```

`infra/kubernetes/secret.example.yaml` documents the contract but is not included by Kustomize. Never commit a populated secret.

### 2. Apply the platform

```bash
kubectl apply -k infra
kubectl rollout status statefulset/postgres -n insurflow
kubectl rollout status deployment/keycloak -n insurflow
kubectl rollout status deployment/risk-service -n insurflow
kubectl rollout status deployment/backend -n insurflow
kubectl rollout status deployment/frontend -n insurflow
```

Map `insurflow.local` to the ingress address in the local hosts file, then open `http://insurflow.local`.

### 3. Inspect scaling and observability

```bash
kubectl get pods,ingress,hpa,pdb -n insurflow
kubectl port-forward service/prometheus 9090:9090 -n insurflow
kubectl port-forward service/grafana 3000:3000 -n insurflow
```

The backend and risk service start with two replicas, declare CPU and memory requests/limits, expose startup/readiness/liveness probes, and scale between two and six replicas at 70% average CPU. Pod disruption budgets keep at least one replica available during voluntary disruption.

## Production adaptations

The included PostgreSQL, Keycloak, Prometheus, and Grafana resources make the portfolio environment reproducible. A production deployment should use managed or highly available equivalents, TLS through cert-manager or a cloud load balancer, an external secret manager, durable observability storage, image digests instead of moving tags, network policies, backups, and environment-specific Kustomize overlays.
