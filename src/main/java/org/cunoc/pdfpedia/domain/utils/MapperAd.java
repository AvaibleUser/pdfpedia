package org.cunoc.pdfpedia.domain.utils;

import org.cunoc.pdfpedia.domain.dto.announcer.AdDto;
import org.cunoc.pdfpedia.domain.dto.announcer.AdPostDto;
import org.cunoc.pdfpedia.domain.dto.announcer.ChargePeriodAdDto;
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
                .expiresAt(LocalDateTime.now().plusDays(chargePeriodAd.getDurationDays())) // calcula dias para que expire el anuncio
                .isDeleted(false)
                .build();
    }

    private ChargePeriodAdDto toDto(ChargePeriodAdEntity chargePeriodAdEntity) {
        return new ChargePeriodAdDto(
                chargePeriodAdEntity.getId(),
                chargePeriodAdEntity.getAdType(),
                chargePeriodAdEntity.getDurationDays(),
                chargePeriodAdEntity.getCost()
        );
    }

    private boolean calcActive(boolean active, LocalDateTime expiresAt) {
        if(!active){
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        return now.isBefore(expiresAt);
    }

    public AdDto toDto(AdEntity adEntity) {
        return new AdDto(
                adEntity.getId(),
                adEntity.getAdvertiser().getId(),
                adEntity.getContent(),
                adEntity.getImageUrl(),
                adEntity.getVideoUrl(),
                adEntity.getCreatedAt(),
                adEntity.getExpiresAt(),
                this.calcActive(adEntity.isActive(), adEntity.getExpiresAt()),
                this.toDto(adEntity.getChargePeriodAd())
        );
    }

}
