package org.cunoc.pdfpedia.domain.dto.announcer;

import org.cunoc.pdfpedia.domain.type.AdType;

import java.math.BigDecimal;

public record ChargePeriodAdDto(
        Long id,
        AdType adType,
        Integer durationDays,
        BigDecimal cost
) {
}
