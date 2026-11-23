package com.showtime.catalog.api;

import com.showtime.catalog.domain.ContentType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class ContentDtos {

    private ContentDtos() {
    }

    public record ContentResponse(Long id, ContentType type, String title, String language, String genre,
                                  int durationMinutes, String certificate, String description, String posterUrl) {
    }

    public record CreateContentRequest(
            @NotNull ContentType type,
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = 50) String language,
            @Size(max = 100) String genre,
            @Positive @Max(600) int durationMinutes,
            @Size(max = 10) String certificate,
            @Size(max = 2000) String description,
            @Size(max = 500) String posterUrl) {
    }
}
