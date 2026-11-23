package com.showtime.catalog.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class CityDtos {

    private CityDtos() {
    }

    public record CityResponse(Long id, String code, String name) {
    }

    public record CreateCityRequest(
            @NotBlank @Pattern(regexp = "[A-Za-z]{2,10}", message = "2-10 letters, e.g. MUM") String code,
            @NotBlank @Size(max = 100) String name) {
    }
}
