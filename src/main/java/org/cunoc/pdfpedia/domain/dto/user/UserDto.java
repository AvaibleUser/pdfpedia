package org.cunoc.pdfpedia.domain.dto.user;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(
        Long id,
        String firstname,
        String lastname,
        String email,
        Instant createdAt,
        Instant updatedAt,
        String roleName) {
}
