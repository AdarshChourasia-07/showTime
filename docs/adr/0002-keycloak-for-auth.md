# ADR 0002: Keycloak for authentication, services as OAuth2 resource servers

**Status:** accepted · **Date:** 2026-11-22

## Context
Customers, venue partners and admins all need to log in, with different permissions.
Writing login, password reset, Google sign-in and token handling by hand is slow and easy to get wrong.

## Decision
Use Keycloak (OIDC) as the identity provider. The Angular apps log in with Authorization Code + PKCE.
Every service validates the JWT itself (Spring Security resource server) and reads roles from `realm_access.roles`.

## Consequences
- No passwords stored in ShowTime's own database.
- Each service checks permissions on the server; Angular route guards are only for UX.
- Keycloak must be running locally (Docker Compose) for protected endpoints to work.
