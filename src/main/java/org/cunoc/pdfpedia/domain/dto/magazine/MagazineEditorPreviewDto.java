package org.cunoc.pdfpedia.domain.dto.magazine;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Builder;

@Builder(toBuilder = true)
public record MagazineEditorPreviewDto(
        Long id,
        String title,
        String description,
        BigDecimal costPerDay,
        String categoryName,
        Instant createdAt,
        Instant updatedAt,
        @JsonUnwrapped MagazineCounts counts) {
}
