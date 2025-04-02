package org.cunoc.pdfpedia.domain.dto.dashboard;

public record SubscriptionsResumeDto(
        Number activePercent,
        Number inactivePercent) {
}
