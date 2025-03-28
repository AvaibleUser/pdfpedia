package org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record MagazineSubscriptions(
        String title,
        String usernameEditor,
        Instant createdAt,
        List<SubscriptionsDto> subscriptionsDtos
) {
}
