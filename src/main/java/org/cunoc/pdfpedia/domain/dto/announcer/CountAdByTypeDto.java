package org.cunoc.pdfpedia.domain.dto.announcer;

import org.cunoc.pdfpedia.domain.type.AdType;

public record CountAdByTypeDto(AdType adType, Integer count) {
    public CountAdByTypeDto(AdType adType, Long count) {
        this(adType, count.intValue());
    }
}
