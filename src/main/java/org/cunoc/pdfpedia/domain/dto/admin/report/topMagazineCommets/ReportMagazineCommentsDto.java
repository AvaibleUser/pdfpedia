package org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ReportMagazineCommentsDto(
        List<MagazineCommentsDto> magazineCommentsDtoList
) {
}
