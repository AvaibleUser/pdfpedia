package org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record SubscriptionsDto(
    String usernameSubscriber,
    String emailSubscriber,
    Instant subscribedAt
) {
}
