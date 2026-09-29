.PHONY: up down logs test

up:
	docker compose up --build

down:
	docker compose down

logs:
	docker compose logs -f

test:
	cd backend && mvn -B test
	cd frontend && npm test -- --watch=false
	cd ml-service && python -m pytest
