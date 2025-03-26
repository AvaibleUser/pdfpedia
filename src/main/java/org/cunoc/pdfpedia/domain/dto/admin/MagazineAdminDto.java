package org.cunoc.pdfpedia.domain.dto.admin;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record MagazineAdminDto(
        Long id,
        String title,
        BigDecimal costPerDay,
        Instant createdAt,
        String username
) {
}
