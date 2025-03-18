package org.cunoc.pdfpedia.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddUserDto(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String firstname,
        @NotBlank String lastname,
        boolean isDeleted,
        String profilePicture,
        String hobbies,
        String description,
        String interestsTopics) {
}
