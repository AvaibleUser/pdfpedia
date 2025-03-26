package org.cunoc.pdfpedia.domain.dto.announcer;

import jakarta.validation.constraints.Positive;
import org.cunoc.pdfpedia.domain.type.AdType;

import java.math.BigDecimal;

public record ChargePeriodAdDto(
        @Positive Long id,
        AdType adType,
        @Positive Integer durationDays,
        @Positive BigDecimal cost
) {
}
