package org.cunoc.pdfpedia.domain.dto.user;

import java.util.Optional;

import org.cunoc.pdfpedia.domain.dto.profile.AddProfileDto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddUserDto(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String password,
        boolean isDeleted,
        Optional<String> profilePicture,
        @JsonUnwrapped @Valid AddProfileDto profile) {
}
