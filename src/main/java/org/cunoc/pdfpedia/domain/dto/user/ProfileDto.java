package org.cunoc.pdfpedia.domain.dto.user;

import org.cunoc.pdfpedia.domain.dto.role.RoleDto;
import org.cunoc.pdfpedia.domain.entity.user.ProfileEntity;

public record ProfileDto(
        Long id,
        String firstname,
        String lastname,
        String hobbies,
        String description,
        String interestsTopics,
        EditorDto user,
        RoleDto role
) {
    public static ProfileDto fromEntity(ProfileEntity profile) {
        return new ProfileDto(
                profile.getId(),
                profile.getFirstname(),
                profile.getLastname(),
                profile.getHobbies(),
                profile.getDescription(),
                profile.getInterestsTopics(),
                EditorDto.fromEntity(profile.getUser()),
                new RoleDto(profile.getUser().getRole().getId(), profile.getUser().getRole().getName())
        );
    }
}
