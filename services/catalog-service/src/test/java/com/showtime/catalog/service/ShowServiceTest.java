package com.showtime.catalog.service;

import com.showtime.catalog.api.ShowDtos.CreateShowRequest;
import com.showtime.catalog.domain.City;
import com.showtime.catalog.domain.Content;
import com.showtime.catalog.domain.ContentType;
import com.showtime.catalog.domain.Screen;
import com.showtime.catalog.domain.SeatCategory;
import com.showtime.catalog.domain.Show;
import com.showtime.catalog.domain.Venue;
import com.showtime.catalog.repository.ShowRepository;
import com.showtime.common.web.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowServiceTest {

    private static final Instant NOW = Instant.parse("2030-01-01T00:00:00Z");
    private static final OffsetDateTime START = OffsetDateTime.of(2030, 1, 10, 10, 0, 0, 0, ZoneOffset.UTC);

    private final ShowRepository shows = mock(ShowRepository.class);
    private final ContentService contentService = mock(ContentService.class);
    private final VenueService venueService = mock(VenueService.class);
    private ShowService service;

    private Screen screen;
    private Content movie;

    @BeforeEach
    void setUp() {
        service = new ShowService(shows, contentService, venueService, Clock.fixed(NOW, ZoneOffset.UTC), "Asia/Kolkata");

        Venue venue = new Venue("Galaxy", "Mumbai", new City("MUM", "Mumbai"), "partner1");
        screen = new Screen(venue, "Audi 1");
        screen.addRow("A", 10, SeatCategory.REGULAR);
        screen.addRow("G", 10, SeatCategory.PREMIUM);
        movie = new Content(ContentType.MOVIE, "Test", "Hindi", null, 120, "U", null, null);

        when(venueService.findScreen(anyLong())).thenReturn(screen);
        when(contentService.find(anyLong())).thenReturn(movie);
    }

    private CreateShowRequest request(OffsetDateTime start, Map<SeatCategory, BigDecimal> prices) {
        return new CreateShowRequest(1L, 1L, start, prices);
    }

    private static Map<SeatCategory, BigDecimal> allPrices() {
        return Map.of(SeatCategory.REGULAR, new BigDecimal("200"), SeatCategory.PREMIUM, new BigDecimal("350"));
    }

    @Test
    void rejectsPartnerWhoDoesNotOwnTheVenue() {
        assertThatThrownBy(() -> service.create(request(START, allPrices()), new Actor("partner2", false)))
                .isInstanceOf(AccessDeniedException.class);
        verify(shows, never()).save(any());
    }

    @Test
    void rejectsMissingPriceForASeatCategory() {
        assertThatThrownBy(() -> service.create(
                request(START, Map.of(SeatCategory.REGULAR, new BigDecimal("200"))), new Actor("partner1", false)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PREMIUM");
    }

    @Test
    void rejectsShowInThePast() {
        OffsetDateTime past = OffsetDateTime.ofInstant(NOW, ZoneOffset.UTC).minusHours(1);
        assertThatThrownBy(() -> service.create(request(past, allPrices()), new Actor("partner1", false)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsOverlappingShowOnSameScreen() {
        Show existing = new Show(movie, screen, START, allPrices());
        when(shows.findScheduledOnScreenBetween(any(), any(), any())).thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.create(request(START.plusMinutes(60), allPrices()), new Actor("partner1", false)))
                .isInstanceOf(ConflictException.class);
        verify(shows, never()).save(any());
    }

    @Test
    void adminCanScheduleOnAnyVenue() {
        when(shows.findScheduledOnScreenBetween(any(), any(), any())).thenReturn(List.of());
        when(shows.save(any(Show.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(request(START, allPrices()), new Actor("admin1", true));

        verify(shows).save(any(Show.class));
    }

    @Test
    void cancelChecksOwnership() {
        Show existing = new Show(movie, screen, START, allPrices());
        when(shows.findDetailedById(5L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.cancel(5L, new Actor("partner2", false)))
                .isInstanceOf(AccessDeniedException.class);
    }
}
