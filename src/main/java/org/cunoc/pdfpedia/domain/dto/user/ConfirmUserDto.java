package org.cunoc.pdfpedia.domain.dto.user;

import jakarta.validation.constraints.NotBlank;

public record ConfirmUserDto(
        @NotBlank String email,
        @NotBlank String code) {
}
