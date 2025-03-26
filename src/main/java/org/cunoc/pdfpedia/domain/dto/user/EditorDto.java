package org.cunoc.pdfpedia.domain.dto.user;

import org.cunoc.pdfpedia.domain.dto.magazine.EditorProfileDto;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;

public record EditorDto(
        Long id,
        String username,
        EditorProfileDto profile
) {
    public static EditorDto fromEntity(UserEntity editor) {
        return new EditorDto(
                editor.getId(),
                editor.getUsername(),
                EditorProfileDto.fromEntity(editor.getProfile())
        );
    }
}