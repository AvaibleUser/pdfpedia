package org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ReportAdvertiserAdViews(
        List<AdvertiserAdViewsDto> advertiserAdViews,
        String range
) {
}
