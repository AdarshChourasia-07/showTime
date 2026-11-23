-- Catalog schema: where and what is playing.

CREATE TABLE city (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code        VARCHAR(10)  NOT NULL UNIQUE,      -- e.g. MUM, BLR, BPL
    name        VARCHAR(100) NOT NULL
);

CREATE TABLE venue (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    address     VARCHAR(300) NOT NULL,
    city_id     BIGINT       NOT NULL REFERENCES city (id),
    partner_id  VARCHAR(100) NOT NULL,              -- Keycloak username of the owning partner
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_venue_city ON venue (city_id);
CREATE INDEX idx_venue_partner ON venue (partner_id);

CREATE TABLE screen (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    venue_id    BIGINT       NOT NULL REFERENCES venue (id),
    name        VARCHAR(50)  NOT NULL,
    total_seats INT          NOT NULL CHECK (total_seats > 0),
    UNIQUE (venue_id, name)
);

CREATE TABLE seat (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    screen_id   BIGINT       NOT NULL REFERENCES screen (id),
    row_label   VARCHAR(5)   NOT NULL,              -- A, B, C ...
    seat_number INT          NOT NULL,
    category    VARCHAR(20)  NOT NULL,              -- REGULAR | PREMIUM | RECLINER
    UNIQUE (screen_id, row_label, seat_number)
);
CREATE INDEX idx_seat_screen ON seat (screen_id);

CREATE TABLE content (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type             VARCHAR(20)  NOT NULL,         -- MOVIE | CONCERT | EVENT
    title            VARCHAR(200) NOT NULL,
    language         VARCHAR(50)  NOT NULL,
    genre            VARCHAR(100),
    duration_minutes INT          NOT NULL CHECK (duration_minutes > 0),
    certificate      VARCHAR(10),                   -- U, UA, A
    description      VARCHAR(2000),
    poster_url       VARCHAR(500)
);

CREATE TABLE shows (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content_id  BIGINT       NOT NULL REFERENCES content (id),
    screen_id   BIGINT       NOT NULL REFERENCES screen (id),
    starts_at   TIMESTAMPTZ  NOT NULL,
    ends_at     TIMESTAMPTZ  NOT NULL,
    status      VARCHAR(20)  NOT NULL,              -- SCHEDULED | CANCELLED
    version     BIGINT       NOT NULL DEFAULT 0,    -- optimistic locking
    CHECK (ends_at > starts_at)
);
CREATE INDEX idx_show_screen_time ON shows (screen_id, starts_at);
CREATE INDEX idx_show_starts_at ON shows (starts_at);

CREATE TABLE show_price (
    show_id     BIGINT        NOT NULL REFERENCES shows (id) ON DELETE CASCADE,
    category    VARCHAR(20)   NOT NULL,
    price       NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    PRIMARY KEY (show_id, category)
);
