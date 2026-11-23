-- Runs once, the first time the Postgres container starts with an empty volume.
-- To re-run it: docker compose down -v && docker compose up -d

-- Keycloak keeps its own data in a separate database.
CREATE DATABASE keycloak OWNER showtime;

-- One schema per service inside the "showtime" database (database-per-service, simplified for local dev).
-- Each service only ever touches its own schema. Flyway creates the tables.
\connect showtime
CREATE SCHEMA IF NOT EXISTS catalog      AUTHORIZATION showtime;
CREATE SCHEMA IF NOT EXISTS inventory    AUTHORIZATION showtime;
CREATE SCHEMA IF NOT EXISTS booking      AUTHORIZATION showtime;
CREATE SCHEMA IF NOT EXISTS payment      AUTHORIZATION showtime;
CREATE SCHEMA IF NOT EXISTS ticketing    AUTHORIZATION showtime;
CREATE SCHEMA IF NOT EXISTS notification AUTHORIZATION showtime;
