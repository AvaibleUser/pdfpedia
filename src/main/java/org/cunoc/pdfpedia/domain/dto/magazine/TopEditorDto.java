package org.cunoc.pdfpedia.domain.dto.magazine;

import lombok.Builder;

@Builder(toBuilder = true)
public record TopEditorDto
        (
        String userName
        ) {

}
