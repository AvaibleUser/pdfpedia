package org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ReportTopMagazineSubscriptions(
        List<MagazineSubscriptions> subscriptions
) {
}
