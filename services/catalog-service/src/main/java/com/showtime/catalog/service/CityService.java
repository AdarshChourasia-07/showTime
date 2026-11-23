package com.showtime.catalog.service;

import com.showtime.catalog.api.CityDtos.CityResponse;
import com.showtime.catalog.api.CityDtos.CreateCityRequest;
import com.showtime.catalog.domain.City;
import com.showtime.catalog.repository.CityRepository;
import com.showtime.common.web.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cities;

    public CityService(CityRepository cities) {
        this.cities = cities;
    }

    @Transactional(readOnly = true)
    public List<CityResponse> list() {
        return cities.findAllByOrderByNameAsc().stream().map(CatalogMapper::toResponse).toList();
    }

    @Transactional
    public CityResponse create(CreateCityRequest request) {
        String code = request.code().toUpperCase();
        if (cities.existsByCode(code)) {
            throw new ConflictException("City " + code + " already exists");
        }
        return CatalogMapper.toResponse(cities.save(new City(code, request.name())));
    }
}
