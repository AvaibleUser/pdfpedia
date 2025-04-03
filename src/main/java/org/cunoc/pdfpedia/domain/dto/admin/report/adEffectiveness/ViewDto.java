package org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness;

import java.time.Instant;

public record ViewDto(
        String urlView,
        Instant createdAtView
) {
}
