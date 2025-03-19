package org.cunoc.pdfpedia.domain.dto.role;

import lombok.Builder;

@Builder(toBuilder = true)
public record RoleDto(
        Long id,
        String name) {
}
