package org.cunoc.pdfpedia.domain.dto.magazine;

import org.cunoc.pdfpedia.domain.entity.user.ProfileEntity;

public record EditorProfileDto(
        Long id,
        String firstName,
        String lastName
) {
    public static EditorProfileDto fromEntity(ProfileEntity profile) {
        return new EditorProfileDto(
                profile.getId(),
                profile.getFirstname(),
                profile.getLastname()
        );
    }
}
