package com.showtime.catalog.service;

import com.showtime.catalog.api.VenueDtos.CreateScreenRequest;
import com.showtime.catalog.api.VenueDtos.CreateVenueRequest;
import com.showtime.catalog.api.VenueDtos.RowRequest;
import com.showtime.catalog.api.VenueDtos.ScreenResponse;
import com.showtime.catalog.api.VenueDtos.SeatLayoutResponse;
import com.showtime.catalog.api.VenueDtos.VenueResponse;
import com.showtime.catalog.domain.City;
import com.showtime.catalog.domain.Screen;
import com.showtime.catalog.domain.Venue;
import com.showtime.catalog.repository.CityRepository;
import com.showtime.catalog.repository.ScreenRepository;
import com.showtime.catalog.repository.VenueRepository;
import com.showtime.common.web.ConflictException;
import com.showtime.common.web.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VenueService {

    private final VenueRepository venues;
    private final ScreenRepository screens;
    private final CityRepository cities;

    public VenueService(VenueRepository venues, ScreenRepository screens, CityRepository cities) {
        this.venues = venues;
        this.screens = screens;
        this.cities = cities;
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> listByCity(String cityCode) {
        return venues.findByCityCode(cityCode.toUpperCase()).stream().map(CatalogMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> listMine(Actor actor) {
        return venues.findByPartnerId(actor.username()).stream().map(CatalogMapper::toResponse).toList();
    }

    @Transactional
    public VenueResponse create(CreateVenueRequest r, Actor actor) {
        City city = cities.findByCode(r.cityCode().toUpperCase())
                .orElseThrow(() -> new NotFoundException("City", r.cityCode()));
        return CatalogMapper.toResponse(venues.save(new Venue(r.name(), r.address(), city, actor.username())));
    }

    @Transactional
    public ScreenResponse addScreen(Long venueId, CreateScreenRequest r, Actor actor) {
        Venue venue = findVenue(venueId);
        requireOwnerOrAdmin(venue, actor);
        if (screens.existsByVenueIdAndNameIgnoreCase(venueId, r.name())) {
            throw new ConflictException("Screen '" + r.name() + "' already exists in this venue");
        }
        Set<String> labels = new HashSet<>();
        for (RowRequest row : r.rows()) {
            if (!labels.add(row.label())) {
                throw new IllegalArgumentException("Row " + row.label() + " appears twice");
            }
        }

        Screen screen = new Screen(venue, r.name());
        r.rows().forEach(row -> screen.addRow(row.label(), row.seats(), row.category()));
        return CatalogMapper.toResponse(screens.save(screen));
    }

    @Transactional(readOnly = true)
    public List<ScreenResponse> listScreens(Long venueId) {
        findVenue(venueId);
        return screens.findByVenueIdOrderByName(venueId).stream().map(CatalogMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SeatLayoutResponse layout(Long screenId) {
        return CatalogMapper.toLayout(findScreen(screenId));
    }

    Venue findVenue(Long id) {
        return venues.findById(id).orElseThrow(() -> new NotFoundException("Venue", id));
    }

    Screen findScreen(Long id) {
        return screens.findById(id).orElseThrow(() -> new NotFoundException("Screen", id));
    }

    static void requireOwnerOrAdmin(Venue venue, Actor actor) {
        if (!actor.admin() && !venue.isOwnedBy(actor.username())) {
            throw new AccessDeniedException("Venue " + venue.getId() + " belongs to another partner");
        }
    }
}
