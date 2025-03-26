package org.cunoc.pdfpedia.domain.dto.admin.report.earnings;

import lombok.Builder;
import org.cunoc.pdfpedia.domain.type.AdType;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record AdReportDto(
        AdType adType,
        String username,
        Instant datePay,
        Integer plan,
        BigDecimal amount
) {
}
