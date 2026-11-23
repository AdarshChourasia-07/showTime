package com.showtime.catalog.api;

import com.showtime.catalog.domain.SeatCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public final class VenueDtos {

    private VenueDtos() {
    }

    public record VenueResponse(Long id, String name, String address, String cityCode, String cityName,
                                String partnerId) {
    }

    public record CreateVenueRequest(
            @NotBlank @Size(max = 150) String name,
            @NotBlank @Size(max = 300) String address,
            @NotBlank String cityCode) {
    }

    /** One row of the seat layout, e.g. row "G", 12 seats, PREMIUM. */
    public record RowRequest(
            @NotBlank @Pattern(regexp = "[A-Z]{1,2}", message = "1-2 capital letters") String label,
            @Min(1) @Max(60) int seats,
            @NotNull SeatCategory category) {
    }

    public record CreateScreenRequest(
            @NotBlank @Size(max = 50) String name,
            @NotEmpty @Size(max = 40) List<@Valid RowRequest> rows) {
    }

    public record ScreenResponse(Long id, Long venueId, String name, int totalSeats) {
    }

    public record SeatResponse(Long id, String row, int number, String label, SeatCategory category) {
    }

    public record SeatLayoutResponse(Long screenId, String screenName, int totalSeats, List<SeatResponse> seats) {
    }
}
