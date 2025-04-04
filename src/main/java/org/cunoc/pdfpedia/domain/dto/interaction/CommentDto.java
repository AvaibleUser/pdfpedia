package org.cunoc.pdfpedia.domain.dto.interaction;

import org.cunoc.pdfpedia.domain.dto.user.EditorDto;
import org.cunoc.pdfpedia.domain.entity.interaction.CommentEntity;

import java.time.Instant;

public record CommentDto(
        Long id,
        EditorDto user,
        String content,
        Instant createdAt
) {
    public static CommentDto fromEntity(CommentEntity entity) {
        return new CommentDto(
                entity.getId(),
                EditorDto.fromEntity(entity.getUser()),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
