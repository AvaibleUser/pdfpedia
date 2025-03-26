package org.cunoc.pdfpedia.domain.dto.issue;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record MagazineIssueDto(
        Long id,
        String title,
        String pdfUrl,
        Long magazineId,
        String magazineTitle,
        Instant publishedAt,
        Instant updatedAt) {
}
