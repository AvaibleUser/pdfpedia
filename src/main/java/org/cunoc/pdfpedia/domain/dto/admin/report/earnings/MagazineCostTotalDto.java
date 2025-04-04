package org.cunoc.pdfpedia.domain.dto.admin.report.earnings;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record MagazineCostTotalDto(
        String title,
        BigDecimal costPerDay,
        Instant createdAt,
        String username,
        BigDecimal costTotal
) {
}
