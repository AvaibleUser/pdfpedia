package org.cunoc.pdfpedia.domain.dto.monetary;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WalletDto(
        @Positive Long id,
        @Positive BigDecimal balance
) {
}


