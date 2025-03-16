package org.cunoc.pdfpedia.domain.dto.user;

import java.util.Optional;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddUserDto(
        @NotBlank String username,
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank String email,
        @NotBlank String password,
        boolean isDeleted,
        Optional<String> profilePicture,
        Optional<String> hobbies,
        Optional<String> description) {
}
