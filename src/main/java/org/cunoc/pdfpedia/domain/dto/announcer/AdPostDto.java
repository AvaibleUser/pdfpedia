package org.cunoc.pdfpedia.domain.dto.announcer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdPostDto(
        @Positive @NotNull Long chargePeriodAd,
        @NotBlank String content,
        @NotBlank String imageUrl,
        @NotBlank String videoUrl,
        @NotBlank String duration

) {
}
