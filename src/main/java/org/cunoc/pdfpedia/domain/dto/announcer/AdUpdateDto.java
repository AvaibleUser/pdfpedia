package org.cunoc.pdfpedia.domain.dto.announcer;

import jakarta.validation.constraints.NotBlank;

public record AdUpdateDto(
        @NotBlank String content,
        @NotBlank String imageUrl,
        @NotBlank String videoUrl
) {
}
