package org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record CommentDto(
        String usernameComment,
        String emailComment,
        String contentComment,
        Instant commentAt
) {
}
