package org.cunoc.pdfpedia.domain.utils;

import org.cunoc.pdfpedia.domain.dto.announcer.AdDto;
import org.cunoc.pdfpedia.domain.dto.announcer.AdPostDto;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MapperAd {



    public AdEntity toEntity(AdPostDto adPostDto, UserEntity advertiser, ChargePeriodAdEntity chargePeriodAd) {
        return AdEntity.builder()
                .advertiser(advertiser)
                .chargePeriodAd(chargePeriodAd)
                .content(adPostDto.content())
                .imageUrl(adPostDto.imageUrl())
                .videoUrl(adPostDto.videoUrl())
                .expiresAt(LocalDateTime.now().plusDays(Integer.parseInt(adPostDto.duration())))
                .isDeleted(false)
                .build();
    }

    public AdDto toDto(AdEntity adEntity) {
        return new AdDto(
                adEntity.getId(),
                adEntity.getAdvertiser().getId(),
                adEntity.getChargePeriodAd().getId(),
                adEntity.getContent(),
                adEntity.getImageUrl(),
                adEntity.getVideoUrl(),
                adEntity.getCreatedAt(),
                adEntity.getExpiresAt()
        );
    }


}
