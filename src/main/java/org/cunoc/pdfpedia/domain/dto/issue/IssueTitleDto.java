package org.cunoc.pdfpedia.domain.dto.issue;

import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record IssueTitleDto(
        Optional<String> title) {
}
