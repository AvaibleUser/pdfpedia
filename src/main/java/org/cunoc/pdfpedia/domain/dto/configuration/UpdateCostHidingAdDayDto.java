package org.cunoc.pdfpedia.domain.dto.configuration;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record UpdateCostHidingAdDayDto(
       @Positive BigDecimal costHidingAdDay
) {
}
