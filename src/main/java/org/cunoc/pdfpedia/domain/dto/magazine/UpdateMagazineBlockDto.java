package org.cunoc.pdfpedia.domain.dto.magazine;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record UpdateMagazineBlockDto(
        @NotNull @Future @JsonFormat(pattern = "yyyy-MM-dd") LocalDate adBlockingExpirationDate) {
}
