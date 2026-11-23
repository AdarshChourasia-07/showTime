package com.showtime.catalog.service;

import com.showtime.catalog.api.CityDtos.CityResponse;
import com.showtime.catalog.api.ContentDtos.ContentResponse;
import com.showtime.catalog.api.ShowDtos.ShowDetail;
import com.showtime.catalog.api.ShowDtos.ShowSummary;
import com.showtime.catalog.api.VenueDtos.ScreenResponse;
import com.showtime.catalog.api.VenueDtos.SeatLayoutResponse;
import com.showtime.catalog.api.VenueDtos.SeatResponse;
import com.showtime.catalog.api.VenueDtos.VenueResponse;
import com.showtime.catalog.domain.City;
import com.showtime.catalog.domain.Content;
import com.showtime.catalog.domain.Screen;
import com.showtime.catalog.domain.Seat;
import com.showtime.catalog.domain.SeatCategory;
import com.showtime.catalog.domain.Show;
import com.showtime.catalog.domain.Venue;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/** Entity -> response DTO. Call inside a transaction (touches lazy associations). */
final class CatalogMapper {

    private CatalogMapper() {
    }

    static CityResponse toResponse(City city) {
        return new CityResponse(city.getId(), city.getCode(), city.getName());
    }

    static ContentResponse toResponse(Content c) {
        return new ContentResponse(c.getId(), c.getType(), c.getTitle(), c.getLanguage(), c.getGenre(),
                c.getDurationMinutes(), c.getCertificate(), c.getDescription(), c.getPosterUrl());
    }

    static VenueResponse toResponse(Venue v) {
        return new VenueResponse(v.getId(), v.getName(), v.getAddress(), v.getCity().getCode(),
                v.getCity().getName(), v.getPartnerId());
    }

    static ScreenResponse toResponse(Screen s) {
        return new ScreenResponse(s.getId(), s.getVenue().getId(), s.getName(), s.getTotalSeats());
    }

    static SeatLayoutResponse toLayout(Screen s) {
        return new SeatLayoutResponse(s.getId(), s.getName(), s.getTotalSeats(),
                s.getSeats().stream().map(CatalogMapper::toResponse).toList());
    }

    static SeatResponse toResponse(Seat seat) {
        return new SeatResponse(seat.getId(), seat.getRowLabel(), seat.getSeatNumber(), seat.label(),
                seat.getCategory());
    }

    static ShowSummary toSummary(Show s) {
        Venue venue = s.getScreen().getVenue();
        return new ShowSummary(s.getId(), s.getStartsAt(), s.getEndsAt(),
                s.getContent().getId(), s.getContent().getTitle(), s.getContent().getLanguage(),
                venue.getId(), venue.getName(), s.getScreen().getName(), venue.getCity().getCode(),
                s.lowestPrice());
    }

    static ShowDetail toDetail(Show s) {
        Map<SeatCategory, BigDecimal> prices = new EnumMap<>(SeatCategory.class);
        prices.putAll(s.getPrices());
        return new ShowDetail(s.getId(), s.getStatus(), s.getStartsAt(), s.getEndsAt(),
                toResponse(s.getContent()), toResponse(s.getScreen().getVenue()),
                s.getScreen().getId(), s.getScreen().getName(), s.getScreen().getTotalSeats(), prices);
    }
}
