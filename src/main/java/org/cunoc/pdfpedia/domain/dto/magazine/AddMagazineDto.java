package org.cunoc.pdfpedia.domain.dto.magazine;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddMagazineDto(
        @NotBlank String title,
        @NotBlank String description,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate adBlockingExpirationDate,
        boolean disableLikes,
        boolean disableComments,
        boolean disableSuscriptions,
        @Positive long categoryId,
        @NotEmpty List<@NotNull @Positive Long> tagIds) {
}
