package com.showtime.catalog.api;

import com.showtime.catalog.api.ContentDtos.ContentResponse;
import com.showtime.catalog.api.VenueDtos.VenueResponse;
import com.showtime.catalog.domain.SeatCategory;
import com.showtime.catalog.domain.ShowStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

public final class ShowDtos {

    private ShowDtos() {
    }

    public record CreateShowRequest(
            @NotNull Long contentId,
            @NotNull Long screenId,
            @NotNull @Future OffsetDateTime startsAt,
            @NotEmpty Map<SeatCategory, @NotNull @PositiveOrZero BigDecimal> prices) {
    }

    /** Compact row for the "shows in your city" list. */
    public record ShowSummary(Long id, OffsetDateTime startsAt, OffsetDateTime endsAt,
                              Long contentId, String title, String language,
                              Long venueId, String venueName, String screenName, String cityCode,
                              BigDecimal priceFrom) {
    }

    public record ShowDetail(Long id, ShowStatus status, OffsetDateTime startsAt, OffsetDateTime endsAt,
                             ContentResponse content, VenueResponse venue,
                             Long screenId, String screenName, int totalSeats,
                             Map<SeatCategory, BigDecimal> prices) {
    }
}
