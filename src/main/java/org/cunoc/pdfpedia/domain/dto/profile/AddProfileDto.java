package org.cunoc.pdfpedia.domain.dto.profile;

import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

public record AddProfileDto(
        @NotBlank String firstname,
        @NotBlank String lastname,
        Optional<String> hobbies,
        Optional<String> description,
        Optional<String> interestsTopics) {
}
