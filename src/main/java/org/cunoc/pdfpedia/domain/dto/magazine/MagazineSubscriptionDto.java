package org.cunoc.pdfpedia.domain.dto.magazine;

import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;

public record MagazineSubscriptionDto(Long id, String title, String description) {
    public static MagazineSubscriptionDto fromEntity(MagazineEntity magazine) {
        return new MagazineSubscriptionDto(magazine.getId(), magazine.getTitle(), magazine.getDescription());
    }
}