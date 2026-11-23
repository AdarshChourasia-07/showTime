package com.showtime.catalog.api;

import com.showtime.catalog.api.ShowDtos.CreateShowRequest;
import com.showtime.catalog.api.ShowDtos.ShowDetail;
import com.showtime.catalog.api.ShowDtos.ShowSummary;
import com.showtime.catalog.service.Actor;
import com.showtime.catalog.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/catalog/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    /** e.g. GET /api/catalog/shows?city=MUM&date=2026-12-01&contentId=1 */
    @GetMapping
    public List<ShowSummary> search(@RequestParam String city,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                    @RequestParam(required = false) Long contentId) {
        return showService.search(city, date, contentId);
    }

    @GetMapping("/{id}")
    public ShowDetail get(@PathVariable Long id) {
        return showService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN')")
    public ShowDetail create(@Valid @RequestBody CreateShowRequest request, Authentication auth) {
        return showService.create(request, Actor.from(auth));
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN')")
    public void cancel(@PathVariable Long id, Authentication auth) {
        showService.cancel(id, Actor.from(auth));
    }
}
