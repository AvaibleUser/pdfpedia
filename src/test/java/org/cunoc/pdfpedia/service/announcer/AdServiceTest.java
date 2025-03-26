package org.cunoc.pdfpedia.service.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.domain.utils.MapperAd;
import org.cunoc.pdfpedia.repository.announcer.AdRepository;
import org.cunoc.pdfpedia.repository.announcer.ChargePeriodAdRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.cunoc.pdfpedia.service.monetary.PaymentService;
import org.cunoc.pdfpedia.service.monetary.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock
    private AdRepository adRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChargePeriodAdRepository chargePeriodAdRepository;
    @Mock
    private MapperAd mapperAd;
    @Mock
    private WalletService walletService;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private AdService adService;

    private AdPostDto adPostDto;
    private UserEntity advertiser;
    private ChargePeriodAdEntity chargePeriodAd;
    private AdEntity adEntity;
    private AdEntity adEntity1;
    private AdDto adDto;
    private AdDto adDto1;
    private AdUpdateDto adUpdateDto;


    @BeforeEach
    void setUp() {
        adPostDto = new AdPostDto(1L,"Content", "image.jpg", "video.mp4");
        advertiser = new UserEntity();
        advertiser.setId(1L);
        chargePeriodAd = new ChargePeriodAdEntity();
        chargePeriodAd.setDurationDays(2);
        chargePeriodAd.setId(1L);
        adEntity = AdEntity
                .builder()
                .id(1l)
                .advertiser(advertiser)
                .chargePeriodAd(chargePeriodAd)
                .content("Content").imageUrl("https://example")
                .videoUrl("https://example")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        adEntity1 = AdEntity
                .builder()
                .id(2l)
                .advertiser(advertiser)
                .chargePeriodAd(chargePeriodAd)
                .content("Content").imageUrl("https://example")
                .videoUrl("https://example").expiresAt(LocalDateTime.now().plusDays(7)).build();
        adDto = new AdDto(1L, 1L, "Content", "https://example", "https://example", null, LocalDateTime.now().plusDays(7), true, null);
        adDto1 = new AdDto(2L, 1L, "Content", "https://example", "https://example", null, LocalDateTime.now().plusDays(7), true, null);

        adUpdateDto = new AdUpdateDto("Content", "https://url/example/update","https://url/example/update");


    }

    /**
     * test function Create
     */
    @Test
    void givenValidAdPostDto_whenCreate_thenReturnsAdDto() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(advertiser));
        when(chargePeriodAdRepository.findById(1L)).thenReturn(Optional.of(chargePeriodAd));
        when(mapperAd.toEntity(adPostDto, advertiser, chargePeriodAd)).thenReturn(adEntity);
        when(adRepository.save(adEntity)).thenReturn(adEntity);
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        AdDto result = adService.create(adPostDto, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.advertiser()).isEqualTo(1L);
        assertThat(result.content()).isEqualTo("Content");
        verify(walletService).updateDecrease(1L, chargePeriodAd.getCost());
        verify(paymentService).createPaymentPostAd(chargePeriodAd.getCost(), adEntity);
    }

    @Test
    void givenNonExistingUser_whenCreate_thenThrowsValueNotFoundException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adService.create(adPostDto, 1L))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("Usuario no encontrado al realizar una publicación de anuncio");
    }

    @Test
    void givenNonExistingChargePeriod_whenCreate_thenThrowsValueNotFoundException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(advertiser));
        when(chargePeriodAdRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adService.create(adPostDto, 1L))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("Periodo de vigencia no válido al realizar una publicación de anuncio");
    }

    @Test
    void givenDatabaseError_whenCreate_thenThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(advertiser));
        when(chargePeriodAdRepository.findById(1L)).thenReturn(Optional.of(chargePeriodAd));
        when(mapperAd.toEntity(adPostDto, advertiser, chargePeriodAd)).thenReturn(adEntity);
        when(adRepository.save(adEntity)).thenThrow(new DataIntegrityViolationException("Database error"));

        // When & Then
        assertThatThrownBy(() -> adService.create(adPostDto, 1L))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Database error");
    }

    /**
     * tests function findAllByUserId
     */
    @Test
    void shouldReturnListOfAds_WhenAdsExist() {
        // Given
        Long userId = 1L;
        List<AdEntity> adEntities = List.of(this.adEntity, this.adEntity1);
        List<AdDto> adDtos = List.of(this.adDto, this.adDto1);

        given(adRepository.findAllByAdvertiserIdOrderByExpiresAtDesc(userId)).willReturn(adEntities);
        given(mapperAd.toDto(any(AdEntity.class))).willAnswer(invocation -> {
            AdEntity ad = invocation.getArgument(0);
            return new AdDto(ad.getId(), userId, ad.getContent(), ad.getImageUrl(), ad.getVideoUrl(), null, null, true, null);
        });

        // When
        List<AdDto> result = adService.findAllByUserId(userId);

        // Then
        assertFalse(result.isEmpty());
        assertNotNull(result);
        assertThat(result.size()).isEqualTo(adDtos.size());

        then(adRepository).should().findAllByAdvertiserIdOrderByExpiresAtDesc(userId);
        then(mapperAd).should(times(2)).toDto(any(AdEntity.class));
    }

    @Test
    void shouldReturnEmptyList_WhenNoAdsExist() {
        // Given
        Long userId = 1L;
        given(adRepository.findAllByAdvertiserIdOrderByExpiresAtDesc(userId)).willReturn(Collections.emptyList());

        // When
        List<AdDto> result = adService.findAllByUserId(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        then(adRepository).should().findAllByAdvertiserIdOrderByExpiresAtDesc(userId);
        then(mapperAd).should(never()).toDto(any(AdEntity.class));
    }

    /**
     * tests function findAllActiveByUserId
     */
    @Test
    void shouldReturnActiveAdsByUserIdWithoutDateFilter() {
        // Given
        Long userId = 1L;
        when(adRepository.findAllByAdvertiserIdAndIsActiveTrueOrderByExpiresAtDesc(userId))
                .thenReturn(List.of(adEntity, adEntity1));
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);
        when(mapperAd.toDto(adEntity1)).thenReturn(adDto1);

        // When
        List<AdDto> result = adService.findAllActiveByUserId(null, null, userId);

        // Then
        assertEquals(2, result.size());
        assertEquals(adDto.id(), result.get(0).id());
        assertEquals(adDto1.id(), result.get(1).id());
        verify(adRepository, times(1)).findAllByAdvertiserIdAndIsActiveTrueOrderByExpiresAtDesc(userId);
        verify(mapperAd, times(2)).toDto(any(AdEntity.class));
    }

    @Test
    void shouldReturnActiveAdsByUserIdWithDateFilter() {
        // Given
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(6);
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(adRepository.findAllByAdvertiserIdAndIsActiveTrueAndCreatedAtBetweenOrderByExpiresAtDesc(userId, startDate, endDate))
                .thenReturn(List.of(adEntity));
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        List<AdDto> result = adService.findAllActiveByUserId(startDate, endDate, userId);

        // Then
        assertEquals(1, result.size());
        assertEquals(adDto.id(), result.getFirst().id());
        verify(adRepository, times(1)).findAllByAdvertiserIdAndIsActiveTrueAndCreatedAtBetweenOrderByExpiresAtDesc(userId, startDate, endDate);
        verify(mapperAd, times(1)).toDto(any(AdEntity.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoAdsFound() {
        // Given
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(5);
        when(adRepository.findAllByAdvertiserIdAndIsActiveTrueAndCreatedAtBetweenOrderByExpiresAtDesc(userId, startDate, endDate))
                .thenReturn(List.of());

        // When
        List<AdDto> result = adService.findAllActiveByUserId(startDate, endDate, userId);

        // Then
        assertTrue(result.isEmpty());
        verify(adRepository, times(1)).findAllByAdvertiserIdAndIsActiveTrueAndCreatedAtBetweenOrderByExpiresAtDesc(userId, startDate, endDate);
    }

    /**
     * tests function updateDeactivated
     */
    @Test
    void shouldDeactivateAd_WhenAdExists() {
        // Given
        Long adId = 1L;
        when(adRepository.findById(adId)).thenReturn(Optional.of(adEntity));
        when(mapperAd.toDto(any(AdEntity.class))).thenReturn(adDto);

        // When
        AdDto result = adService.updateDeactivated(adId);

        // Then
        assertNotNull(result);
        assertFalse(adEntity.isActive());
        assertEquals(adDto, result);
        verify(adRepository).save(adEntity);
        verify(mapperAd).toDto(adEntity);
    }

    @Test
    void shouldThrowException_WhenAdDoesNotExist() {
        // Given
        Long adId = 999L;
        when(adRepository.findById(adId)).thenReturn(Optional.empty());

        // When & Then
        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class, () -> adService.updateDeactivated(adId));
        assertEquals("Anuncio no encontrado para desactivar", exception.getMessage());
        verify(adRepository, never()).save(any(AdEntity.class));
        verify(mapperAd, never()).toDto(any(AdEntity.class));
    }

    /**
     * tests function updateActive
     */
    @Test
    void givenValidAdIdAndAdPostDtoAndAdvertiserId_whenUpdateActive_thenAdIsActivated() {
        // Given
        when(chargePeriodAdRepository.findById(adPostDto.chargePeriodAd())).thenReturn(Optional.of(chargePeriodAd));
        when(adRepository.findById(1L)).thenReturn(Optional.of(adEntity));
        adEntity.setActive(true);
        when(adRepository.save(any(AdEntity.class))).thenReturn(adEntity);
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        AdDto result = adService.updateActive(1L, adPostDto, 1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isActive());
        verify(walletService, times(1)).updateDecrease(1L, chargePeriodAd.getCost());
        verify(paymentService, times(1)).createPaymentPostAd(chargePeriodAd.getCost(), adEntity);
    }

    @Test
    void givenInvalidChargePeriodAdId_whenUpdateActive_thenThrowValueNotFoundException() {
        // Given
        when(chargePeriodAdRepository.findById(adPostDto.chargePeriodAd())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValueNotFoundException.class, () -> adService.updateActive(1L, adPostDto, 1L));
    }

    @Test
    void givenInvalidAdId_whenUpdateActive_thenThrowValueNotFoundException() {
        // Given
        when(chargePeriodAdRepository.findById(adPostDto.chargePeriodAd())).thenReturn(Optional.of(chargePeriodAd));
        when(adRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValueNotFoundException.class, () -> adService.updateActive(1L, adPostDto, 1L));
    }

    /**
     * tests function update
     */
    @Test
    void givenValidAdIdAndAdUpdateDto_whenUpdate_thenAdIsUpdated() {
        // Given
        when(adRepository.findById(1L)).thenReturn(Optional.of(adEntity));
        when(adRepository.save(any(AdEntity.class))).thenReturn(adEntity);
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        AdDto result = adService.update(1L, adUpdateDto);

        // Then
        assertNotNull(result);
        assertEquals("Content", result.content());
        assertEquals("https://example", result.imageUrl());
        assertEquals("https://example", result.videoUrl());
        verify(adRepository, times(1)).save(adEntity);
    }

    @Test
    void givenInvalidAdId_whenUpdate_thenThrowValueNotFoundException() {
        // Given
        when(adRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValueNotFoundException.class, () -> adService.update(1L, adUpdateDto));
    }

    /**
     * tests function totalAdsByUserId
     */
    @Test
    void givenValidUserId_whenTotalAdsByUserId_thenReturnTotalAdsDto() {
        // Given
        Long userId = 1L;
        int totalAds = 5;
        int activeAds = 3;

        when(adRepository.countAllByAdvertiserId(userId)).thenReturn(totalAds);
        when(adRepository.countAllByAdvertiserIdAndIsActiveTrue(userId)).thenReturn(activeAds);

        // When
        TotalAdsDto result = adService.totalAdsByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(totalAds, result.total());
        assertEquals(activeAds, result.totalActive());
        verify(adRepository, times(1)).countAllByAdvertiserId(userId);
        verify(adRepository, times(1)).countAllByAdvertiserIdAndIsActiveTrue(userId);
    }

    /**
     * tests function getPostMount
     */
    @Test
    void givenValidUserId_whenGetPostMount_thenReturnListOfPostAdMount() {
        // Given
        Long userId = 1L;
        List<PostAdMount> expectedPostAdMounts = Arrays.asList(
                new PostAdMount("01", 5),
                new PostAdMount("02", 3)
        );

        when(adRepository.countAdsByMonth(userId)).thenReturn(expectedPostAdMounts);

        // When
        List<PostAdMount> result = adService.getPostMount(userId);

        // Then
        assertNotNull(result);
        assertEquals(expectedPostAdMounts.size(), result.size());
        assertEquals(expectedPostAdMounts, result);
        verify(adRepository, times(1)).countAdsByMonth(userId);
    }

}