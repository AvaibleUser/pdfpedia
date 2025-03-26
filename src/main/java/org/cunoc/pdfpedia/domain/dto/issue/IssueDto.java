package org.cunoc.pdfpedia.domain.dto.issue;

import java.time.Instant;

public record IssueDto(
        Long id,
        String title,
        String pdfUrl,
        Instant publishedAt,
        Instant updatedAt) {
}
