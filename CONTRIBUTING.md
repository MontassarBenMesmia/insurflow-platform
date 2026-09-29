# Contributing

1. Create a branch from `main`.
2. Keep changes focused and add tests for behavior changes.
3. Run the backend, frontend, and ML test suites before opening a pull request.
4. Never commit API keys, passwords, personal datasets, model artifacts, or `.env` files.
5. Use clear commit messages written in the imperative mood.

## Local checks

```bash
cd backend && mvn test
cd ../frontend && npm ci && npm test -- --watch=false && npm run build
cd ../ml-service && python -m pip install -r requirements-dev.txt && pytest
```
