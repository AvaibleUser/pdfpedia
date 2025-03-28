package org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record MagazineProjectionCommentsDto(
        Long magazineId,
        String nameComment,
        String nameEditor,
        String email,
        String title,
        String content,
        Instant commentAt,
        Instant createdAt
) {
}
