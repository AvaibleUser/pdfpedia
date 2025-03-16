package org.cunoc.pdfpedia.domain.dto.user;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(
        Long id,
        String username,
        String email,
        String profilePicture,
        boolean isDeleted,
        Instant createdAt,
        Instant updatedAt,
        String roleName,
        @JsonProperty("firstname") String profileFirstname,
        @JsonProperty("lastname") String profileLastname,
        @JsonProperty("hobbies") String profileHobbies,
        @JsonProperty("description") String profileDescription,
        @JsonProperty("interestsTopics") String profileInterestsTopics) {
}
