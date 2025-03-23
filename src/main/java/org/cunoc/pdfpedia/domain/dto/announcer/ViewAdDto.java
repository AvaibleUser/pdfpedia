package org.cunoc.pdfpedia.domain.dto.announcer;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record ViewAdDto(
        String urlView,
        Instant createdAt
) {
}
