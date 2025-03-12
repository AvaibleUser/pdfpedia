package org.cunoc.pdfpedia.domain.dto.user;

import jakarta.validation.constraints.NotBlank;

public record AuthUserDto(
        @NotBlank String email,
        @NotBlank String password) {
}
