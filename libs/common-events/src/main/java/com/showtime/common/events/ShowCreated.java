package com.showtime.common.events;

import java.time.OffsetDateTime;

/** Published by catalog-service when a show is scheduled. Inventory uses it to prepare the seat map. */
public record ShowCreated(
        long showId,
        long screenId,
        long contentId,
        String cityCode,
        OffsetDateTime startsAt,
        int totalSeats) {
}
