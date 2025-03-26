package org.cunoc.pdfpedia.domain.dto.magazine;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record UpdateMagazineDto(
        Optional<String> title,
        Optional<String> description,
        @JsonFormat(pattern = "yyyy-MM-dd") Optional<@Future LocalDate> adBlockingExpirationDate,
        Optional<Boolean> disableLikes,
        Optional<Boolean> disableComments,
        Optional<Boolean> disableSuscriptions,
        Optional<@Positive Long> categoryId,
        Optional<@NotEmpty List<@Positive Long>> tagIds) {
}
