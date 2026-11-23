package com.showtime.catalog.service;

import com.showtime.catalog.api.ShowDtos.CreateShowRequest;
import com.showtime.catalog.api.ShowDtos.ShowDetail;
import com.showtime.catalog.api.ShowDtos.ShowSummary;
import com.showtime.catalog.domain.Content;
import com.showtime.catalog.domain.Screen;
import com.showtime.catalog.domain.SeatCategory;
import com.showtime.catalog.domain.Show;
import com.showtime.catalog.domain.ShowStatus;
import com.showtime.catalog.repository.ShowRepository;
import com.showtime.common.web.ConflictException;
import com.showtime.common.web.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Service
public class ShowService {

    private final ShowRepository shows;
    private final ContentService contentService;
    private final VenueService venueService;
    private final Clock clock;
    private final ZoneId zone;

    public ShowService(ShowRepository shows, ContentService contentService, VenueService venueService,
                       Clock clock, @Value("${showtime.default-zone}") String zone) {
        this.shows = shows;
        this.contentService = contentService;
        this.venueService = venueService;
        this.clock = clock;
        this.zone = ZoneId.of(zone);
    }

    /** Shows in a city on a given local date (default: today), optionally for one movie/event. */
    @Transactional(readOnly = true)
    public List<ShowSummary> search(String cityCode, LocalDate date, Long contentId) {
        LocalDate day = date != null ? date : LocalDate.now(clock.withZone(zone));
        OffsetDateTime from = day.atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime to = day.plusDays(1).atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime now = OffsetDateTime.now(clock);
        if (from.isBefore(now)) {
            from = now; // never list shows that already started
        }
        return shows.search(cityCode.toUpperCase(), from, to, contentId, ShowStatus.SCHEDULED).stream()
                .map(CatalogMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShowDetail get(Long id) {
        return CatalogMapper.toDetail(shows.findDetailedById(id).orElseThrow(() -> new NotFoundException("Show", id)));
    }

    @Transactional
    public ShowDetail create(CreateShowRequest r, Actor actor) {
        Screen screen = venueService.findScreen(r.screenId());
        VenueService.requireOwnerOrAdmin(screen.getVenue(), actor);
        Content content = contentService.find(r.contentId());

        if (!r.startsAt().isAfter(OffsetDateTime.now(clock))) {
            throw new IllegalArgumentException("Show must start in the future");
        }

        Set<SeatCategory> missing = screen.seatCategories();
        missing.removeAll(r.prices().keySet());
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing price for seat categories " + missing);
        }

        OffsetDateTime start = r.startsAt();
        OffsetDateTime end = start.plusMinutes(content.getDurationMinutes());
        boolean clash = shows.findScheduledOnScreenBetween(screen.getId(),
                        start.minus(Show.TURNAROUND), end.plus(Show.TURNAROUND))
                .stream()
                .anyMatch(existing -> existing.overlaps(start, end));
        if (clash) {
            throw new ConflictException("Screen " + screen.getName() + " is already booked around " + start
                    + " (shows need a " + Show.TURNAROUND.toMinutes() + " min gap)");
        }

        Show saved = shows.save(new Show(content, screen, start, r.prices()));
        return CatalogMapper.toDetail(saved);
    }

    @Transactional
    public void cancel(Long id, Actor actor) {
        Show show = shows.findDetailedById(id).orElseThrow(() -> new NotFoundException("Show", id));
        VenueService.requireOwnerOrAdmin(show.getScreen().getVenue(), actor);
        show.cancel();
        // Phase 2: publish show.cancelled so Booking can refund existing tickets.
    }
}
