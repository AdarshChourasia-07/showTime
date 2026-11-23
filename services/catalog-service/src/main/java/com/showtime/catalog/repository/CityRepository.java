package com.showtime.catalog.repository;

import com.showtime.catalog.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByCode(String code);

    boolean existsByCode(String code);

    List<City> findAllByOrderByNameAsc();
}
