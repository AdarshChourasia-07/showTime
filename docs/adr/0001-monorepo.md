# ADR 0001: One monorepo with a Maven multi-module build

**Status:** accepted · **Date:** 2026-11-22

## Context
ShowTime has ~9 services, 2 shared libraries and 2 frontends, built by one developer.
Separate repos would mean separate versioning, CI pipelines and pull requests for every cross-service change.

## Decision
Keep everything in one repository with a parent `pom.xml`. Shared code lives in `libs/`.
Service boundaries are enforced by package and module structure, and by each service owning its own database schema.

## Consequences
- One clone, one build, one CI pipeline; changing an event contract and its consumers is one pull request.
- Services must not import each other's modules, only `libs/*`. Reviewed in pull requests.
- If the project ever had separate teams, services could be split into their own repos later.
