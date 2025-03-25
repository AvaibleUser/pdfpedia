package org.cunoc.pdfpedia.domain.dto.dashboard;

import lombok.Builder;

@Builder(toBuilder = true)
public record CountRegisterByRolDto(
        String typeUser,
        Long count
) {
}
