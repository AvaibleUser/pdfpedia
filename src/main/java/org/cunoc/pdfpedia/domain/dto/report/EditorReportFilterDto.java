package org.cunoc.pdfpedia.domain.dto.report;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder(toBuilder = true)
public record EditorReportFilterDto(
        Optional<@Positive Long> magazineId,
        Optional<@PastOrPresent LocalDate> startDate,
        Optional<@PastOrPresent LocalDate> endDate) {
}
