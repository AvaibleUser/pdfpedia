package org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record MagazineCommentsDto(
        String title,
        String usernameEditor,
        Instant createdAt,
        List<CommentDto> comments
) {
}
