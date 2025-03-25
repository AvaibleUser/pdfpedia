package org.cunoc.pdfpedia.domain.dto.configuration;

import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record ConfigurationDto(
        Long id,
        BigDecimal costMagazineDay,
        BigDecimal costHidingAdDay
) {
}
