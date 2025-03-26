package org.cunoc.pdfpedia.domain.dto.magazine;

import org.cunoc.pdfpedia.domain.dto.category.CategoryDto;
import org.cunoc.pdfpedia.domain.dto.user.EditorDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;

public record MagazineItemDto(
        Long id,
        String title,
        String description,
        CategoryDto category,
        EditorDto editor,
        int likeCount,
        boolean disableLikes,
        boolean disableComments,
        boolean disableSuscriptions
) {
    public static MagazineItemDto fromEntity(MagazineEntity entity) {
        return new MagazineItemDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                CategoryDto.fromEntity(entity.getCategory()),
                EditorDto.fromEntity(entity.getEditor()),
                entity.getLikes().size(),
                entity.isDisableLikes(),
                entity.isDisableComments(),
                entity.isDisableSuscriptions()
        );
    }
}