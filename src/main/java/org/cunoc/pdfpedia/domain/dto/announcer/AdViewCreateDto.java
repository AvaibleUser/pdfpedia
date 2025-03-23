package org.cunoc.pdfpedia.domain.dto.announcer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AdViewCreateDto(
        @Positive Long adId,
        @NotBlank String urlView
) {
}
