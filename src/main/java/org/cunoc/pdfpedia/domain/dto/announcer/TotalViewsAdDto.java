package org.cunoc.pdfpedia.domain.dto.announcer;

import lombok.Builder;

@Builder(toBuilder = true)
public record TotalViewsAdDto(
        Integer total
) {
}
