package org.cunoc.pdfpedia.domain.utils;

import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.AdViewsEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.type.AdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MapperAdTest {

    @InjectMocks
    private MapperAd mapperAd;

    private AdPostDto adPostDto;
    private UserEntity advertiser;
    private ChargePeriodAdEntity chargePeriodAd;
    private AdEntity adEntity;
    private AdViewsEntity adViewsEntity;

    @BeforeEach
    void setUp() {
        adPostDto = new AdPostDto(1L, "Content", "https://example.com/image.jpg", "https://example.com/video.mp4");

        advertiser = new UserEntity();
        advertiser.setId(1L);

        chargePeriodAd = new ChargePeriodAdEntity();
        chargePeriodAd.setId(1L);
        chargePeriodAd.setAdType(AdType.TEXT);
        chargePeriodAd.setDurationDays(30);
        chargePeriodAd.setCost(BigDecimal.valueOf(100));

        adEntity = AdEntity.builder()
                .id(1L)
                .advertiser(advertiser)
                .chargePeriodAd(chargePeriodAd)
                .content("Content")
                .imageUrl("https://example.com/image.jpg")
                .videoUrl("https://example.com/video.mp4")
                .expiresAt(LocalDateTime.now().plusDays(30))
                .isActive(true)
                .isDeleted(false)
                .build();

        adViewsEntity = AdViewsEntity.builder()
                .id(1L)
                .ad(adEntity)
                .urlView("https://example.com/view")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void givenAdPostDto_whenToEntity_thenReturnAdEntity() {
        // When
        AdEntity result = mapperAd.toEntity(adPostDto, advertiser, chargePeriodAd);

        // Then
        assertNotNull(result);
        assertEquals(adPostDto.content(), result.getContent());
        assertEquals(adPostDto.imageUrl(), result.getImageUrl());
        assertEquals(adPostDto.videoUrl(), result.getVideoUrl());
        assertEquals(advertiser, result.getAdvertiser());
        assertEquals(chargePeriodAd, result.getChargePeriodAd());
        assertFalse(result.isDeleted());
    }

    @Test
    void givenChargePeriodAdEntity_whenToDto_thenReturnChargePeriodAdDto() {
        // When
        ChargePeriodAdDto result = mapperAd.toDto(chargePeriodAd);

        // Then
        assertNotNull(result);
        assertEquals(chargePeriodAd.getId(), result.id());
        assertEquals(chargePeriodAd.getAdType(), result.adType());
        assertEquals(chargePeriodAd.getDurationDays(), result.durationDays());
        assertEquals(chargePeriodAd.getCost(), result.cost());
    }

    @Test
    void givenActiveAd_whenCalcActive_thenReturnTrue() {
        // Given
        boolean active = true;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // When
        boolean result = mapperAd.calcActive(active, expiresAt);

        // Then
        assertTrue(result);
    }

    @Test
    void givenInactiveAd_whenCalcActive_thenReturnFalse() {
        // Given
        boolean active = false;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // When
        boolean result = mapperAd.calcActive(active, expiresAt);

        // Then
        assertFalse(result);
    }

    @Test
    void givenExpiredAd_whenCalcActive_thenReturnFalse() {
        // Given
        boolean active = true;
        LocalDateTime expiresAt = LocalDateTime.now().minusDays(1);

        // When
        boolean result = mapperAd.calcActive(active, expiresAt);

        // Then
        assertFalse(result);
    }

    @Test
    void givenAdEntity_whenToDto_thenReturnAdDto() {
        // When
        AdDto result = mapperAd.toDto(adEntity);

        // Then
        assertNotNull(result);
        assertEquals(adEntity.getId(), result.id());
        assertEquals(adEntity.getAdvertiser().getId(), result.advertiser());
        assertEquals(adEntity.getContent(), result.content());
        assertEquals(adEntity.getImageUrl(), result.imageUrl());
        assertEquals(adEntity.getVideoUrl(), result.videoUrl());
        assertEquals(adEntity.getCreatedAt(), result.createdAt());
        assertEquals(adEntity.getExpiresAt(), result.expiresAt());
        assertTrue(result.isActive());
        assertEquals(chargePeriodAd.getId(), result.changePeriodAd().id());
    }

    @Test
    void givenAdViewsEntity_whenToDto_thenReturnViewAdDto() {
        // When
        ViewAdDto result = mapperAd.toDto(adViewsEntity);

        // Then
        assertNotNull(result);
        assertEquals(adViewsEntity.getUrlView(), result.urlView());
        assertEquals(adViewsEntity.getCreatedAt(), result.createdAt());
    }

    @Test
    void givenSetOfAdViewsEntities_whenToDto_thenReturnListOfViewAdDto() {
        // Given
        Set<AdViewsEntity> viewAds = Set.of(adViewsEntity);

        // When
        List<ViewAdDto> result = mapperAd.toDto(viewAds);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(adViewsEntity.getUrlView(), result.getFirst().urlView());
        assertEquals(adViewsEntity.getCreatedAt(), result.getFirst().createdAt());
    }

    @Test
    void givenAdEntity_whenAdViewsDto_thenReturnAdViewReportDto() {
        // Given
        adEntity.setViewAds(Set.of(adViewsEntity));

        // When
        AdViewReportDto result = mapperAd.adViewsDto(adEntity);

        // Then
        assertNotNull(result);
        assertEquals(adEntity.getId(), result.adDto().id());
        assertEquals(1, result.viewsAdDto().size());
        assertEquals(adViewsEntity.getUrlView(), result.viewsAdDto().getFirst().urlView());
    }

}