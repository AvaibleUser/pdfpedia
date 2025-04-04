package org.cunoc.pdfpedia.domain.dto.admin;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record UpdateCostMagazineDto(
        @Positive BigDecimal costPerDay
) {
}
