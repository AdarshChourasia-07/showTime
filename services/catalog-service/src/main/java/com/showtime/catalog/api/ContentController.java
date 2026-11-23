package com.showtime.catalog.api;

import com.showtime.catalog.api.ContentDtos.ContentResponse;
import com.showtime.catalog.api.ContentDtos.CreateContentRequest;
import com.showtime.catalog.domain.ContentType;
import com.showtime.catalog.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Movies, concerts and events. */
@RestController
@RequestMapping("/api/catalog/contents")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public List<ContentResponse> list(@RequestParam(required = false) ContentType type,
                                      @RequestParam(required = false) String q) {
        return contentService.list(type, q);
    }

    @GetMapping("/{id}")
    public ContentResponse get(@PathVariable Long id) {
        return contentService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    public ContentResponse create(@Valid @RequestBody CreateContentRequest request) {
        return contentService.create(request);
    }
}
