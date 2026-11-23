package com.showtime.catalog.repository;

import com.showtime.catalog.domain.Content;
import com.showtime.catalog.domain.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByTypeOrderByTitle(ContentType type);

    List<Content> findByTitleContainingIgnoreCaseOrderByTitle(String text);
}
