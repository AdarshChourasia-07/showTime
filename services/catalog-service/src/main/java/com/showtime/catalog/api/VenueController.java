package com.showtime.catalog.api;

import com.showtime.catalog.api.VenueDtos.CreateScreenRequest;
import com.showtime.catalog.api.VenueDtos.CreateVenueRequest;
import com.showtime.catalog.api.VenueDtos.ScreenResponse;
import com.showtime.catalog.api.VenueDtos.SeatLayoutResponse;
import com.showtime.catalog.api.VenueDtos.VenueResponse;
import com.showtime.catalog.service.Actor;
import com.showtime.catalog.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping("/venues")
    public List<VenueResponse> byCity(@RequestParam String city) {
        return venueService.listByCity(city);
    }

    /** Venues owned by the logged-in partner (partner portal). */
    @GetMapping("/venues/mine")
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN')")
    public List<VenueResponse> mine(Authentication auth) {
        return venueService.listMine(Actor.from(auth));
    }

    @PostMapping("/venues")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN')")
    public VenueResponse create(@Valid @RequestBody CreateVenueRequest request, Authentication auth) {
        return venueService.create(request, Actor.from(auth));
    }

    @GetMapping("/venues/{venueId}/screens")
    public List<ScreenResponse> screens(@PathVariable Long venueId) {
        return venueService.listScreens(venueId);
    }

    @PostMapping("/venues/{venueId}/screens")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN')")
    public ScreenResponse addScreen(@PathVariable Long venueId, @Valid @RequestBody CreateScreenRequest request,
                                    Authentication auth) {
        return venueService.addScreen(venueId, request, Actor.from(auth));
    }

    /** Seat layout used to draw the seat map. Live availability comes from inventory-service. */
    @GetMapping("/screens/{screenId}/layout")
    public SeatLayoutResponse layout(@PathVariable Long screenId) {
        return venueService.layout(screenId);
    }
}
