package org.cunoc.pdfpedia.domain.dto.announcer;

public record PostAdMount(String month, Integer count) {
    public PostAdMount(String month, Long count) {
        this(month, count.intValue());
    }
}

