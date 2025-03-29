package org.cunoc.pdfpedia.domain.dto.admin.report.earnings;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record MagazineReportDto(
        String title,
        String editor,
        Instant datePay,
        BigDecimal amount
) {
}
