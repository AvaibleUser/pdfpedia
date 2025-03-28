package org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer;

import lombok.Builder;
import org.cunoc.pdfpedia.domain.type.AdType;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record AdReportEmailDto(
        AdType adType,
        String username,
        Instant datePay,
        Integer plan,
        BigDecimal amount,
        String email
) {
}
