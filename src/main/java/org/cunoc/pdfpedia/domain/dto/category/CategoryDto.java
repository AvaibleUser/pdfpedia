package org.cunoc.pdfpedia.domain.dto.category;

import java.time.Instant;

import lombok.Builder;
import org.cunoc.pdfpedia.domain.entity.magazine.CategoryEntity;

@Builder(toBuilder = true)
public record CategoryDto(
        long id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt) {

    public static CategoryDto fromEntity(CategoryEntity category) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription(), category.getCreatedAt(), category.getUpdatedAt());
    }
}
