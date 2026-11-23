# ShowTime

Scalable event ticketing platform (movies, concerts, events) built as Spring Boot microservices,
designed so that no seat is ever sold twice, even when thousands of users book at once.

**Status: Phase 0 done** (setup, gateway, catalog service, Angular shell). Phase 1 adds seat holds, booking, payment and tickets.

## Tech stack

| Layer | Tech |
| --- | --- |
| Backend | Java 25, Spring Boot 4.1, Spring Cloud 2025.1 (Gateway), Spring Security (OAuth2 resource server), Spring Data JPA, Flyway |
| Auth | Keycloak 26 (OIDC), roles CUSTOMER / PARTNER / ADMIN |
| Data | PostgreSQL 18 (schema per service), Redis 8, Apache Kafka 4 (KRaft) |
| Frontend | Angular 22 (signals, zoneless, `httpResource`), Angular Material |
| Tooling | Maven multi-module, Docker Compose, GitHub Actions |

## Project structure

```
showtime/
├─ docs/                  design notes and architecture decision records (ADRs)
├─ libs/
│  ├─ common-events/      Kafka event contracts shared by services
│  └─ common-web/         error responses (ProblemDetail), Keycloak role mapping, base starters
├─ services/
│  ├─ gateway/            :8080  single entry point, routes /api/** , CORS
│  ├─ catalog-service/    :8081  cities, venues, screens, seat layouts, movies/events, shows
│  ├─ inventory-service/  :8082  (skeleton) seat availability and holds
│  ├─ booking-service/    :8083  (skeleton) orders and booking saga
│  └─ payment-service/    :8084  (skeleton) payments and refunds
├─ frontend/customer-app/ :4200  Angular customer app
├─ deploy/                docker-compose.yml, Postgres init, Keycloak realm
├─ http/                  ready-made API requests for IntelliJ's HTTP client
└─ .run/                  shared IntelliJ run configurations
```

Every service uses the same layers: `api` (controllers, DTOs) → `service` (business rules) → `domain` (entities) → `repository`, plus `config`.

## Prerequisites

- JDK 25 (Temurin), IntelliJ IDEA, Git
- Docker Desktop (WSL 2 backend on Windows)
- Node.js 22 or 24 LTS, Angular CLI 22: `npm i -g @angular/cli@22`

## First-time setup

1. **Start the infrastructure** (Postgres, Redis, Kafka, Kafka UI, Keycloak):
   ```
   cd deploy
   docker compose up -d
   ```
   Keycloak takes 30–60 seconds the first time. Check http://localhost:8180 opens.

2. **Open the project in IntelliJ**: *File → Open →* select the root `pom.xml` → *Open as Project*.
   Set the project SDK to JDK 25 (*File → Project Structure*). Maven downloads dependencies on first import.

3. **Build once** (IntelliJ Maven panel → `showtime-parent` → Lifecycle → `install`, or `mvn install` in a terminal).
   *Optional:* run `mvn wrapper:wrapper` once to add the Maven wrapper (`mvnw`) to the repo.

4. **Run the services**: pick the **All services** run configuration (top-right dropdown) and press Run.
   Or run `gateway (8080)` and `catalog-service (8081)` only; that's all the customer app needs today.

5. **Run the customer app**:
   ```
   cd frontend/customer-app
   npm install
   ng serve
   ```
   Open http://localhost:4200, pick a date from tomorrow onwards and click a showtime to see the seat map.

## Local URLs

| What | URL |
| --- | --- |
| Customer app | http://localhost:4200 |
| API gateway | http://localhost:8080 |
| Catalog Swagger UI | http://localhost:8081/api/catalog/swagger-ui.html |
| Keycloak admin console | http://localhost:8180 (admin / admin) |
| Kafka UI | http://localhost:8090 |
| Postgres | localhost:5432, db `showtime`, user/pass `showtime` |

## Test users (Keycloak realm `showtime`)

| Username | Password | Role |
| --- | --- | --- |
| customer1 | customer1 | CUSTOMER |
| partner1 | partner1 | PARTNER (owns the seeded venues) |
| admin1 | admin1 | ADMIN |

New sign-ups get the CUSTOMER role automatically.

## Trying the API

Open `http/catalog.http` in IntelliJ and click the run arrow next to each request.
It fetches tokens for the test users first, then calls public and protected endpoints,
including a 403 example (customer trying an admin action) and a 409 example (overlapping show).

## Tests

```
mvn test                         # backend unit tests
cd frontend/customer-app && ng test --watch=false
```

## Business rules implemented so far (catalog)

- Browsing is public; creating venues, screens, content and shows needs PARTNER or ADMIN.
- A partner can only manage venues they own; admins can manage all.
- A show cannot overlap another show on the same screen, including a 30-minute turnaround gap.
- Every seat category on the screen must have a price.
- Show times are stored as `TIMESTAMPTZ` (UTC) and searched by local day (Asia/Kolkata).

## Troubleshooting

- **No shows listed**: the seed data creates shows for the 7 days after the database was first created.
  To refresh: `docker compose down -v && docker compose up -d` (wipes local data), or schedule new shows as `partner1`.
- **401 on protected endpoints**: tokens expire after 30 minutes; re-run the token request in `catalog.http`.
- **Service can't connect to Postgres**: check `docker compose ps` shows `showtime-postgres` as healthy.
- **Machine slow**: run only the services you need; each has `-Xmx256m` set in its run configuration.
  Limit Docker's memory in `%UserProfile%\.wslconfig` (e.g. `memory=6GB`).

## Roadmap

- [x] Phase 0: repo, infra, Keycloak, gateway, catalog service, Angular shell
- [ ] Phase 1: seat holds (Redis Lua), booking + payment saga, QR tickets, Keycloak login in the app
- [ ] Phase 2: Kafka + outbox, fair flash-sale queue, group split payment, search, load tests
- [ ] Phase 3: partner/admin portal, analytics, Kubernetes, observability
