package org.cunoc.pdfpedia.domain.dto.category;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record CategoryDto(
        long id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt) {
}
