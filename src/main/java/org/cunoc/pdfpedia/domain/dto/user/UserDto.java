package org.cunoc.pdfpedia.domain.dto.user;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(
        Long id,
        String name,
        String lastname,
        String email,
        String nit,
        String cui,
        String phone,
        Boolean activeMfa,
        Instant createdAt,
        Instant updatedAt) {
}
