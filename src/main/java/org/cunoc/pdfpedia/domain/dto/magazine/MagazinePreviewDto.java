package org.cunoc.pdfpedia.domain.dto.magazine;

import java.time.Instant;
import java.util.Set;

import lombok.Builder;

@Builder(toBuilder = true)
public record MagazinePreviewDto(
        Long id,
        String title,
        String description,
        String editorUsername,
        String categoryName,
        Set<String> tagsName,
        Set<String> subscriptionsUsername,
        Set<String> likesUsername,
        Set<String> commentsUsername,
        Instant createdAt,
        Instant updatedAt) {
}
