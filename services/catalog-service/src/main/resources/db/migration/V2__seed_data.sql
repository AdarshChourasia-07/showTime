-- Demo data so the app has something to show on first run.
-- Venues belong to the test partner "partner1" from the Keycloak realm.

INSERT INTO city (code, name) VALUES
    ('MUM', 'Mumbai'),
    ('BLR', 'Bengaluru'),
    ('BPL', 'Bhopal');

INSERT INTO venue (name, address, city_id, partner_id) VALUES
    ('Galaxy Cinemas Phoenix', 'Lower Parel, Mumbai', (SELECT id FROM city WHERE code = 'MUM'), 'partner1'),
    ('Orion Multiplex',        'Rajajinagar, Bengaluru', (SELECT id FROM city WHERE code = 'BLR'), 'partner1'),
    ('DB City Screens',        'MP Nagar, Bhopal', (SELECT id FROM city WHERE code = 'BPL'), 'partner1');

-- One screen per venue: rows A-F REGULAR (12 seats), G-H PREMIUM (12 seats), J RECLINER (8 seats) = 104 seats
INSERT INTO screen (venue_id, name, total_seats)
SELECT id, 'Audi 1', 104 FROM venue;

INSERT INTO seat (screen_id, row_label, seat_number, category)
SELECT s.id, r.label, n, r.category
FROM screen s
CROSS JOIN (VALUES ('A','REGULAR',12), ('B','REGULAR',12), ('C','REGULAR',12), ('D','REGULAR',12),
                   ('E','REGULAR',12), ('F','REGULAR',12), ('G','PREMIUM',12), ('H','PREMIUM',12),
                   ('J','RECLINER',8)) AS r(label, category, seats)
CROSS JOIN LATERAL generate_series(1, r.seats) AS n;

INSERT INTO content (type, title, language, genre, duration_minutes, certificate, description) VALUES
    ('MOVIE',   'The Last Signal',   'Hindi',   'Sci-Fi, Thriller', 142, 'UA', 'A radio engineer picks up a message from a ship lost 40 years ago.'),
    ('MOVIE',   'Monsoon Wedding Run','English', 'Comedy',           118, 'U',  'A groom has six hours to cross Mumbai in the rain.'),
    ('CONCERT', 'Indie Nights Live', 'English', 'Music',            180, 'U',  'Five indie bands, one stage.');

-- Shows for the next 7 days at 10:00, 14:00 and 19:00 IST on every screen, rotating the content.
INSERT INTO shows (content_id, screen_id, starts_at, ends_at, status)
SELECT c.id,
       sc.id,
       ((current_date + d) + t.slot) AT TIME ZONE 'Asia/Kolkata',
       ((current_date + d) + t.slot) AT TIME ZONE 'Asia/Kolkata' + make_interval(mins => c.duration_minutes),
       'SCHEDULED'
FROM screen sc
CROSS JOIN generate_series(1, 7) AS d
CROSS JOIN (VALUES (TIME '10:00', 1), (TIME '14:00', 2), (TIME '19:00', 3)) AS t(slot, content_no)
JOIN content c ON c.id = (SELECT id FROM content ORDER BY id OFFSET t.content_no - 1 LIMIT 1);

INSERT INTO show_price (show_id, category, price)
SELECT s.id, p.category, p.price
FROM shows s
CROSS JOIN (VALUES ('REGULAR', 250.00), ('PREMIUM', 400.00), ('RECLINER', 750.00)) AS p(category, price);
