package org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record AdvertiserAdViewsDto(
        String username,
        String email,
        Long idUser,
        List<AdViewsDto> adViewsDtos
) {
}
