package org.cunoc.pdfpedia.domain.dto.announcer;


import java.time.Instant;
import java.time.LocalDateTime;

public record AdDto(
        Long id,
        Long advertiser,
        String content,
        String imageUrl,
        String videoUrl,
        Instant createdAt,
        LocalDateTime expiresAt,
        boolean isActive,
        ChargePeriodAdDto changePeriodAd
) {
}
