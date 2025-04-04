package org.cunoc.pdfpedia.domain.dto.magazine;

import java.util.List;
import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateMagazineDto(
        Optional<String> title,
        Optional<String> description,
        Optional<Boolean> disableLikes,
        Optional<Boolean> disableComments,
        Optional<Boolean> disableSuscriptions,
        Optional<Long> categoryId,
        Optional<List<Long>> tagIds) {
}
