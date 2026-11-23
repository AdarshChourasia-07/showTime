package com.showtime.catalog.service;

import com.showtime.catalog.api.ContentDtos.ContentResponse;
import com.showtime.catalog.api.ContentDtos.CreateContentRequest;
import com.showtime.catalog.domain.Content;
import com.showtime.catalog.domain.ContentType;
import com.showtime.catalog.repository.ContentRepository;
import com.showtime.common.web.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContentService {

    private final ContentRepository contents;

    public ContentService(ContentRepository contents) {
        this.contents = contents;
    }

    @Transactional(readOnly = true)
    public List<ContentResponse> list(ContentType type, String query) {
        List<Content> result;
        if (query != null && !query.isBlank()) {
            result = contents.findByTitleContainingIgnoreCaseOrderByTitle(query.trim());
            if (type != null) {
                result = result.stream().filter(c -> c.getType() == type).toList();
            }
        } else if (type != null) {
            result = contents.findByTypeOrderByTitle(type);
        } else {
            result = contents.findAll(Sort.by("title"));
        }
        return result.stream().map(CatalogMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ContentResponse get(Long id) {
        return CatalogMapper.toResponse(find(id));
    }

    @Transactional
    public ContentResponse create(CreateContentRequest r) {
        Content content = new Content(r.type(), r.title(), r.language(), r.genre(), r.durationMinutes(),
                r.certificate(), r.description(), r.posterUrl());
        return CatalogMapper.toResponse(contents.save(content));
    }

    Content find(Long id) {
        return contents.findById(id).orElseThrow(() -> new NotFoundException("Content", id));
    }
}
