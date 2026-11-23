package com.showtime.catalog.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ShowTest {

    private static final OffsetDateTime TEN_AM = OffsetDateTime.of(2030, 1, 10, 10, 0, 0, 0, ZoneOffset.UTC);

    /** A 120-minute show from 10:00 to 12:00; the screen is busy until 12:30 including turnaround. */
    private final Show show = new Show(
            new Content(ContentType.MOVIE, "Test", "Hindi", null, 120, "U", null, null),
            null, TEN_AM, Map.of(SeatCategory.REGULAR, new BigDecimal("200")));

    @Test
    void endTimeIsStartPlusDuration() {
        assertThat(show.getEndsAt()).isEqualTo(TEN_AM.plusMinutes(120));
    }

    @Test
    void showStartingDuringTurnaroundOverlaps() {
        assertThat(show.overlaps(TEN_AM.plusMinutes(140), TEN_AM.plusMinutes(260))).isTrue();
    }

    @Test
    void showStartingAfterTurnaroundDoesNotOverlap() {
        assertThat(show.overlaps(TEN_AM.plusMinutes(150), TEN_AM.plusMinutes(270))).isFalse();
    }

    @Test
    void showEndingTooCloseBeforeStartOverlaps() {
        // 07:30-09:45 ends 15 minutes before 10:00, less than the 30 minute gap
        assertThat(show.overlaps(TEN_AM.minusMinutes(150), TEN_AM.minusMinutes(15))).isTrue();
    }

    @Test
    void cancelledShowNeverOverlaps() {
        show.cancel();
        assertThat(show.overlaps(TEN_AM, TEN_AM.plusMinutes(60))).isFalse();
    }
}
