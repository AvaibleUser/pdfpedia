package org.cunoc.pdfpedia.domain.dto.dashboard;

import lombok.Builder;

@Builder(toBuilder = true)
public record ChartPlotDto(
        String groupBy,
        long likes,
        long comments,
        long subscriptions) {

}