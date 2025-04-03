package org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness;

import lombok.Builder;
import org.cunoc.pdfpedia.domain.type.AdType;

import java.time.Instant;

@Builder(toBuilder = true)
public record ViewsProjection(
        //views
        String urlView,
        Instant createdAtView,
        //Ad
        Instant createdAtAd,
        AdType adType,
        Long idAd,
        Integer durationDays,
        // advertiser
        String username,
        String email,
        Long idUser
) {
}
