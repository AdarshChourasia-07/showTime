# ADR 0003: One PostgreSQL schema per service (locally)

**Status:** accepted · **Date:** 2026-11-22

## Context
Database-per-service keeps services independent, but running six Postgres instances on a 16 GB laptop is wasteful.

## Decision
Locally, run one Postgres container with one schema per service (`catalog`, `inventory`, `booking`, ...).
Each service's Flyway migrations and Hibernate default schema point only at its own schema.
No service reads another service's tables; data is shared through APIs and Kafka events.

## Consequences
- Same isolation rules as separate databases, at a fraction of the memory.
- Moving a service to its own database later only changes its connection URL.
