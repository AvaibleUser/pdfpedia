package org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer;

import lombok.Builder;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record PaymentPostAdPerAnnouncerDto(
        BigDecimal amountTotal,
        List<AdReportEmailDto> adReportDtos,
        String username
) {
}
