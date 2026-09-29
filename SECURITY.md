# Security policy

This is a portfolio demonstration, not a production insurance system. Do not use it with real customer or financial data.

## Reporting a vulnerability

Report security issues privately through the repository owner's GitHub profile. Do not open a public issue containing credentials, personal data, or exploit details.

## Development safeguards

- OAuth 2.0 / OpenID Connect authorization is handled by Keycloak.
- Browser authentication uses Authorization Code Flow with PKCE.
- The API validates JWT issuer, signature, roles, and request fields.
- Secrets are supplied through environment variables and excluded from Git.
- The included demo credentials are local-only and must be replaced outside local development.
