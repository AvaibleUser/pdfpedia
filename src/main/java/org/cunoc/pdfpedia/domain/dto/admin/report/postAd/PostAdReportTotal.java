package org.cunoc.pdfpedia.domain.dto.admin.report.postAd;

import lombok.Builder;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record PostAdReportTotal(
        List<AdReportDto> adReportDto,
        BigDecimal totalAdPost
) {
}
