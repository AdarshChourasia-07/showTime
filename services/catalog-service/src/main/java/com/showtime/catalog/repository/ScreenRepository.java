package com.showtime.catalog.repository;

import com.showtime.catalog.domain.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, Long> {

    boolean existsByVenueIdAndNameIgnoreCase(Long venueId, String name);

    List<Screen> findByVenueIdOrderByName(Long venueId);
}
