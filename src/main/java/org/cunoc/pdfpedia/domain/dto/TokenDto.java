package org.cunoc.pdfpedia.domain.dto;

import org.cunoc.pdfpedia.domain.dto.user.UserDto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record TokenDto(
        String token,
        @JsonUnwrapped UserDto user) {
}
