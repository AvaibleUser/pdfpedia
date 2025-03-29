package org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record MagazineProjectionDto(
        Long magazineId,
        String nameSusbcription,
        String nameEditor,
        String email,
        String title,
        Instant subscribedAt,
        Instant createdAt
) {
}
