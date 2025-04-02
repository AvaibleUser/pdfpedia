package org.cunoc.pdfpedia.domain.dto.dashboard;

import lombok.Builder;

@Builder(toBuilder = true)
public record StatsDto(
        StatDto wallet,
        StatDto likes,
        StatDto comments,
        StatDto subscriptions) {

    public StatsDto(Number wto, Number wtr, Number lto, Number ltr, Number cto, Number ctr, Number sto, Number str) {
        this(new StatDto(wto, wtr), new StatDto(lto, ltr), new StatDto(cto, ctr), new StatDto(sto, str));
    }
}
