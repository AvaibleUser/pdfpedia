package org.cunoc.pdfpedia.domain.dto.admin.report.earnings;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record EarningsReport(
        BigDecimal totalEarnings,
        BigDecimal totalAdPost,
        BigDecimal totalAdBlocks,
        BigDecimal totalCostPerDay,
        BigDecimal totalIncome,
        List<AdReportDto> adReportDto,
        List<MagazineReportDto> magazineReportDto,
        List<MagazineCostTotalDto> magazineCostTotalDto,
        String range
) {
}
