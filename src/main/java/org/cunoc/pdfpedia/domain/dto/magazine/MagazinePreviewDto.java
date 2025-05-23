package org.cunoc.pdfpedia.domain.dto.magazine;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Builder;

@Builder(toBuilder = true)
public record MagazinePreviewDto(
        Long id,
        String title,
        String description,
        String editorUsername,
        String categoryName,
        Instant createdAt,
        Instant updatedAt,
        @JsonUnwrapped MagazineCounts counts) {
}
