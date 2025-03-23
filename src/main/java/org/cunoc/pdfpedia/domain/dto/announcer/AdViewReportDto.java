package org.cunoc.pdfpedia.domain.dto.announcer;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record AdViewReportDto(
        AdDto adDto,
        List<ViewAdDto> viewsAdDto
) {
}
