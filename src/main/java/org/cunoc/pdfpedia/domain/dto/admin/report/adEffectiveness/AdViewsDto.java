package org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness;

import lombok.Builder;
import org.cunoc.pdfpedia.domain.type.AdType;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record AdViewsDto(
        Instant createdAtAd,
        AdType adType,
        Long idAd,
        Integer durationDays,
        List<ViewDto> views
) {
}
