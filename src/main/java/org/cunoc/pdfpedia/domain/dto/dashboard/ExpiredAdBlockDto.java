package org.cunoc.pdfpedia.domain.dto.dashboard;

import java.time.LocalDate;

import lombok.Builder;

@Builder(toBuilder = true)
public record ExpiredAdBlockDto(
        LocalDate expirationDate,
        String magazineTitle) {
}
