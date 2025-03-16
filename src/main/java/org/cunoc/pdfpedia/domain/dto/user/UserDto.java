package org.cunoc.pdfpedia.domain.dto.user;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(
        Long id,
        String username,
        String firstname,
        String lastname,
        String email,
        String profilePicture,
        String hobbies,
        String description,
        boolean isDeleted,
        Instant createdAt,
        Instant updatedAt,
        String roleName) {
}
