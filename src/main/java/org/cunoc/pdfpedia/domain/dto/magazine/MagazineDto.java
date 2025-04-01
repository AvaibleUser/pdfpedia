package org.cunoc.pdfpedia.domain.dto.magazine;

import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;

@Builder(toBuilder = true)
public record MagazineDto(
        Long id,
        String title,
        String description,
        LocalDate adBlockingExpirationDate,
        boolean disableLikes,
        boolean disableComments,
        boolean disableSuscriptions,
        Long categoryId,
        @JsonIgnore String tagIds) {

    @JsonGetter("tagIds")
    public Set<Long> getTagIds() {
        return tagIds == null ? null
                : Stream.of(tagIds.split(","))
                        .map(Long::parseLong)
                        .collect(toSet());
    }
}
