package org.cunoc.pdfpedia.domain.dto.magazine;

import org.cunoc.pdfpedia.domain.entity.magazine.MagazineIssueEntity;

import java.time.Instant;

public record MagazineIssueDto(Long id, String pdfUrl, Instant publishedAt) {
    public static MagazineIssueDto fromEntity(MagazineIssueEntity entity) {
        return new MagazineIssueDto(entity.getId(), entity.getPdfUrl(), entity.getPublishedAt());
    }
}