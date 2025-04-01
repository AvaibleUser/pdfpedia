package org.cunoc.pdfpedia.service.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.domain.dto.dashboard.AnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.magazine.TopEditorDto;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.domain.utils.MapperAd;
import org.cunoc.pdfpedia.repository.announcer.AdRepository;
import org.cunoc.pdfpedia.repository.announcer.ChargePeriodAdRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.cunoc.pdfpedia.service.monetary.IPaymentService;
import org.cunoc.pdfpedia.service.monetary.IWalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private IWalletService walletService;
    @Mock
    private IPaymentService paymentService;

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
    private LocalDate startDate;
    private LocalDate endDate;
    private Instant startInstant;
    private Instant endInstant;


    @BeforeEach
    void setUp() {
        adPostDto = new AdPostDto(1L,"Content", "image.jpg", "video.mp4");
        advertiser = new UserEntity();
        advertiser.setId(1L);
        advertiser.setUsername("topEditor");
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

        startDate = LocalDate.of(2025, 1, 1);
        endDate = LocalDate.of(2025, 12, 31);
        startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        endInstant = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
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

    /**
     * tests function getTotalPostAd
     */
    @Test
    void givenNullDates_whenGetTotalPostAd_thenReturnTotalCount() {
        // Given
        long expectedCount = 10L;
        when(adRepository.count()).thenReturn(expectedCount);

        // When
        TotalTarjertDto result = adService.getTotalPostAd(null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.total()).isEqualTo(expectedCount);

        verify(adRepository).count();
        verifyNoMoreInteractions(adRepository);
    }

    @Test
    void givenValidDateRange_whenGetTotalPostAd_thenReturnFilteredCount() {
        // Given
        long expectedCount = 5L;
        when(adRepository.countAllByCreatedAtBetween(startInstant, endInstant))
                .thenReturn(expectedCount);

        // When
        TotalTarjertDto result = adService.getTotalPostAd(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.total()).isEqualTo(expectedCount);

        verify(adRepository).countAllByCreatedAtBetween(startInstant, endInstant);
        verifyNoMoreInteractions(adRepository);
    }

    @Test
    void givenStartDateNull_whenGetTotalPostAd_thenReturnTotalCount() {
        // Given
        long expectedCount = 8L;
        when(adRepository.count()).thenReturn(expectedCount);

        // When
        TotalTarjertDto result = adService.getTotalPostAd(null, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.total()).isEqualTo(expectedCount);

        verify(adRepository).count();
    }

    @Test
    void givenEndDateNull_whenGetTotalPostAd_thenReturnTotalCount() {
        // Given
        long expectedCount = 7L;
        when(adRepository.count()).thenReturn(expectedCount);

        // When
        TotalTarjertDto result = adService.getTotalPostAd(startDate, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.total()).isEqualTo(expectedCount);

        verify(adRepository).count();
    }

    @Test
    void givenStartDateAfterEndDate_whenGetTotalPostAd_thenReturnZero() {
        // Given
        LocalDate invalidStartDate = endDate.plusDays(1);
        Instant invalidStartInstant = invalidStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(adRepository.countAllByCreatedAtBetween(invalidStartInstant, endInstant))
                .thenReturn(0L);

        // When
        TotalTarjertDto result = adService.getTotalPostAd(invalidStartDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.total()).isZero();
    }


    @Test
    void givenNoAdsExist_whenGetTotalPostAd_thenReturnZero() {
        // Given
        when(adRepository.count()).thenReturn(0L);

        // When
        TotalTarjertDto result = adService.getTotalPostAd(null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.total()).isZero();
    }

    /**
     * tests function getTopPostAd
     */
    @Test
    void givenNullDates_whenGetTopPostAd_thenReturnTopEditor() {
        // Given
        when(adRepository.findAllByIsDeletedFalseOrderByAdvertiser(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(adEntity)));

        // When
        TopEditorDto result = adService.getTopPostAd(null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.userName()).isEqualTo("topEditor");

        verify(adRepository).findAllByIsDeletedFalseOrderByAdvertiser(any(PageRequest.class));
    }

    @Test
    void givenValidDates_whenGetTopPostAd_thenReturnFilteredTopEditor() {
        // Given
        when(adRepository.findAllByIsDeletedFalseAndCreatedAtBetweenOrderByAdvertiser(
                eq(startInstant), eq(endInstant), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(adEntity)));

        // When
        TopEditorDto result = adService.getTopPostAd(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.userName()).isEqualTo("topEditor");

        verify(adRepository).findAllByIsDeletedFalseAndCreatedAtBetweenOrderByAdvertiser(
                eq(startInstant), eq(endInstant), any(PageRequest.class));
        verifyNoMoreInteractions(adRepository);
    }

    @Test
    void givenNoAdsFound_whenGetTopPostAd_thenThrowException() {
        // Given
        when(adRepository.findAllByIsDeletedFalseOrderByAdvertiser(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // When / Then
        assertThatThrownBy(() -> adService.getTopPostAd(null, null))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessageContaining("No hay registros");
    }

    @Test
    void givenNullStartDate_whenGetTopPostAd_thenReturnUnfiltered() {
        // Given
        when(adRepository.findAllByIsDeletedFalseOrderByAdvertiser(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(adEntity)));

        // When
        TopEditorDto result = adService.getTopPostAd(null, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.userName()).isEqualTo("topEditor");

        verify(adRepository).findAllByIsDeletedFalseOrderByAdvertiser(any(PageRequest.class));
    }

    @Test
    void givenNullEndDate_whenGetTopPostAd_thenReturnUnfiltered() {
        // Given
        when(adRepository.findAllByIsDeletedFalseOrderByAdvertiser(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(adEntity)));

        // When
        TopEditorDto result = adService.getTopPostAd(startDate, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.userName()).isEqualTo("topEditor");

        verify(adRepository).findAllByIsDeletedFalseOrderByAdvertiser(any(PageRequest.class));
    }

    @Test
    void givenStartDateAfterEndDate_whenGetTopPostAd_thenThrowException() {
        // Given
        LocalDate invalidStartDate = endDate.plusDays(1);
        Instant invalidStartInstant = invalidStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(adRepository.findAllByIsDeletedFalseAndCreatedAtBetweenOrderByAdvertiser(
                eq(invalidStartInstant), eq(endInstant), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // When / Then
        assertThatThrownBy(() -> adService.getTopPostAd(invalidStartDate, endDate))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessageContaining("No hay registros");
    }

    @Test
    void givenRepositoryThrowsException_whenGetTopPostAd_thenPropagateException() {
        // Given
        when(adRepository.findAllByIsDeletedFalseOrderByAdvertiser(any(PageRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When / Then
        assertThatThrownBy(() -> adService.getTopPostAd(null, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }

    /**
     * tests function getAdCountsByMonth
     */
    @Test
    void givenNullDates_whenGetAdCountsByMonth_thenReturnAllCounts() {
        // Given
        List<PostAdMount> expectedCounts = List.of(
                new PostAdMount("January", 10),
                new PostAdMount("February", 15)
        );

        when(adRepository.countAdsByMonth()).thenReturn(expectedCounts);

        // When
        List<PostAdMount> result = adService.getAdCountsByMonth(null, null);

        // Then
        assertThat(result).isNotNull();

        verify(adRepository).countAdsByMonth();
        verifyNoMoreInteractions(adRepository);
    }

    @Test
    void givenValidDates_whenGetAdCountsByMonth_thenReturnFilteredCounts() {
        // Given
        List<PostAdMount> expectedCounts = List.of(
                new PostAdMount("March", 5),
                new PostAdMount("April", 8)
        );

        when(adRepository.countAdsByMonthByBetween(startInstant, endInstant))
                .thenReturn(expectedCounts);

        // When
        List<PostAdMount> result = adService.getAdCountsByMonth(startDate, endDate);

        // Then
        assertThat(result).isNotNull();

        verify(adRepository).countAdsByMonthByBetween(startInstant, endInstant);
        verifyNoMoreInteractions(adRepository);
    }

    @Test
    void givenStartDateNull_whenGetAdCountsByMonth_thenReturnAllCounts() {
        // Given
        List<PostAdMount> expectedCounts = List.of(
                new PostAdMount("May", 12)
        );

        when(adRepository.countAdsByMonth()).thenReturn(expectedCounts);

        // When
        List<PostAdMount> result = adService.getAdCountsByMonth(null, endDate);

        // Then
        assertThat(result).isNotNull();

        verify(adRepository).countAdsByMonth();
    }

    @Test
    void givenEndDateNull_whenGetAdCountsByMonth_thenReturnAllCounts() {
        // Given
        List<PostAdMount> expectedCounts = List.of(
                new PostAdMount("June", 7)
        );

        when(adRepository.countAdsByMonth()).thenReturn(expectedCounts);

        // When
        List<PostAdMount> result = adService.getAdCountsByMonth(startDate, null);

        // Then
        assertThat(result).isNotNull();

        verify(adRepository).countAdsByMonth();
    }

    @Test
    void givenStartDateAfterEndDate_whenGetAdCountsByMonth_thenReturnEmptyList() {
        // Given
        LocalDate invalidStartDate = endDate.plusDays(1);
        Instant invalidStartInstant = invalidStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(adRepository.countAdsByMonthByBetween(invalidStartInstant, endInstant))
                .thenReturn(Collections.emptyList());

        // When
        List<PostAdMount> result = adService.getAdCountsByMonth(invalidStartDate, endDate);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void givenRepositoryReturnsEmpty_whenGetAdCountsByMonth_thenReturnEmptyList() {
        // Given
        when(adRepository.countAdsByMonth()).thenReturn(Collections.emptyList());

        // When
        List<PostAdMount> result = adService.getAdCountsByMonth(null, null);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void givenRepositoryThrowsException_whenGetAdCountsByMonth_thenPropagateException() {
        // Given
        when(adRepository.countAdsByMonth())
                .thenThrow(new RuntimeException("Database error"));

        // When / Then
        assertThatThrownBy(() -> adService.getAdCountsByMonth(null, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }

    @Test
    void givenLongCounts_whenGetAdCountsByMonth_thenConvertToInteger() {
        // Given
        List<PostAdMount> expectedCounts = List.of(
                new PostAdMount("July", Integer.MAX_VALUE)
        );

        when(adRepository.countAdsByMonth()).thenReturn(
                List.of(new PostAdMount("July", (long) Integer.MAX_VALUE))
        );

        // When
        List<PostAdMount> result = adService.getAdCountsByMonth(null, null);

        // Then
        assertThat(result).isNotNull();
    }

    /**
     * tests function findAll
     */
    @Test
    void findAll_shouldReturnAdsOrderedByExpiresAtDesc() {
        // Given
        List<AdEntity> adEntities = List.of(adEntity1, adEntity); // Note the order
        when(adRepository.findAllByOrderByExpiresAtDesc()).thenReturn(adEntities);
        when(mapperAd.toDto(adEntity1)).thenReturn(adDto1);
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        List<AdDto> result = adService.findAll();

        // Then
        assertAll(
                () -> assertEquals(2, result.size(), "Should return 2 ads"),
                () -> assertEquals(adDto1.id(), result.get(0).id(), "First ad should be the one with later expiration"),
                () -> assertEquals(adDto.id(), result.get(1).id(), "Second ad should be the one with earlier expiration"),
                () -> verify(adRepository).findAllByOrderByExpiresAtDesc(),
                () -> verify(mapperAd).toDto(adEntity1),
                () -> verify(mapperAd).toDto(adEntity),
                () -> verifyNoMoreInteractions(adRepository, mapperAd)
        );
    }

    @Test
    void findAll_emptyRepository_shouldReturnEmptyList() {
        // Given
        when(adRepository.findAllByOrderByExpiresAtDesc()).thenReturn(Collections.emptyList());

        // When
        List<AdDto> result = adService.findAll();

        // Then
        assertAll(
                () -> assertTrue(result.isEmpty(), "Should return empty list"),
                () -> verify(adRepository).findAllByOrderByExpiresAtDesc(),
                () -> verifyNoInteractions(mapperAd),
                () -> verifyNoMoreInteractions(adRepository)
        );
    }

    @Test
    void findAll_shouldCallMapperForEachEntity() {
        // Given
        List<AdEntity> adEntities = List.of(adEntity, adEntity1);
        when(adRepository.findAllByOrderByExpiresAtDesc()).thenReturn(adEntities);
        when(mapperAd.toDto(any(AdEntity.class))).thenReturn(adDto);

        // When
        List<AdDto> result = adService.findAll();

        // Then
        assertAll(
                () -> assertEquals(2, result.size(), "Should return 2 ads"),
                () -> verify(mapperAd).toDto(adEntity),
                () -> verify(mapperAd).toDto(adEntity1),
                () -> verifyNoMoreInteractions(mapperAd)
        );
    }


    @Test
    void findAll_singleAd_shouldConvertCorrectly() {
        // Given
        when(adRepository.findAllByOrderByExpiresAtDesc()).thenReturn(List.of(adEntity));
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        List<AdDto> result = adService.findAll();

        // Then
        assertAll(
                () -> assertEquals(1, result.size(), "Should return single ad"),
                () -> assertEquals(adDto, result.get(0), "Converted DTO should match"),
                () -> assertEquals(adEntity.getId(), result.get(0).id(), "ID should match"),
                () -> assertEquals(adEntity.getContent(), result.get(0).content(), "Content should match")
        );
    }

    @Test
    void findAll_shouldMaintainRepositoryOrder() {
        // Given
        List<AdEntity> adEntities = List.of(adEntity, adEntity1); // Specific order
        when(adRepository.findAllByOrderByExpiresAtDesc()).thenReturn(adEntities);
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);
        when(mapperAd.toDto(adEntity1)).thenReturn(adDto1);

        // When
        List<AdDto> result = adService.findAll();

        // Then
        assertAll(
                () -> assertEquals(2, result.size(), "Should return 2 ads"),
                () -> assertEquals(adDto.id(), result.get(0).id(), "First ad ID should match"),
                () -> assertEquals(adDto1.id(), result.get(1).id(), "Second ad ID should match"),
                () -> assertTrue(result.get(0).expiresAt().isBefore(result.get(1).expiresAt()),
                        "Ads should be in descending order of expiresAt")
        );
    }

    /**
     * tests function findAllAnnouncers
     */
    @Test
    void findAllAnnouncers_shouldReturnListOfAnnouncers() {
        // Given
        List<AnnouncersDto> expectedAnnouncers = List.of(
                new AnnouncersDto(1L, "announcer1"),
                new AnnouncersDto(2L, "announcer2")
        );

        when(userRepository.findAllByRole_Name("ANNOUNCER", AnnouncersDto.class))
                .thenReturn(expectedAnnouncers);

        // When
        List<AnnouncersDto> result = adService.findAllAnnouncers();

        // Then
        assertAll(
                () -> assertEquals(expectedAnnouncers.size(), result.size(),
                        "Should return same number of announcers"),
                () -> assertEquals(expectedAnnouncers.get(0).id(), result.get(0).id(),
                        "First announcer ID should match"),
                () -> assertEquals(expectedAnnouncers.get(1).username(), result.get(1).username(),
                        "Second announcer username should match"),
                () -> verify(userRepository).findAllByRole_Name("ANNOUNCER", AnnouncersDto.class),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(adRepository, chargePeriodAdRepository, mapperAd,
                        walletService, paymentService)
        );
    }

    @Test
    void findAllAnnouncers_noAnnouncers_shouldReturnEmptyList() {
        // Given
        when(userRepository.findAllByRole_Name("ANNOUNCER", AnnouncersDto.class))
                .thenReturn(Collections.emptyList());

        // When
        List<AnnouncersDto> result = adService.findAllAnnouncers();

        // Then
        assertAll(
                () -> assertTrue(result.isEmpty(), "Should return empty list"),
                () -> verify(userRepository).findAllByRole_Name("ANNOUNCER", AnnouncersDto.class),
                () -> verifyNoMoreInteractions(userRepository)
        );
    }

    @Test
    void findAllAnnouncers_shouldUseExactRoleName() {
        // Given
        when(userRepository.findAllByRole_Name(eq("ANNOUNCER"), eq(AnnouncersDto.class)))
                .thenReturn(List.of(new AnnouncersDto(1L, "test")));

        // When
        adService.findAllAnnouncers();

        // Then
        verify(userRepository).findAllByRole_Name("ANNOUNCER", AnnouncersDto.class);
    }

    @Test
    void findAllAnnouncers_shouldUseCorrectDtoProjection() {
        // Given
        when(userRepository.findAllByRole_Name(eq("ANNOUNCER"), eq(AnnouncersDto.class)))
                .thenReturn(List.of(new AnnouncersDto(1L, "test")));

        // When
        adService.findAllAnnouncers();

        // Then
        verify(userRepository).findAllByRole_Name(anyString(), eq(AnnouncersDto.class));
    }

    @Test
    void findAllAnnouncers_singleAnnouncer_shouldReturnSingleItemList() {
        // Given
        AnnouncersDto singleAnnouncer = new AnnouncersDto(1L, "single_announcer");
        when(userRepository.findAllByRole_Name("ANNOUNCER", AnnouncersDto.class))
                .thenReturn(List.of(singleAnnouncer));

        // When
        List<AnnouncersDto> result = adService.findAllAnnouncers();

        // Then
        assertAll(
                () -> assertEquals(1, result.size(), "Should return single announcer"),
                () -> assertEquals(singleAnnouncer, result.get(0), "DTO should match"),
                () -> assertEquals(singleAnnouncer.username(), result.get(0).username(),
                        "Username should match")
        );
    }

    @Test
    void findAllAnnouncers_shouldOnlyCallUserRepository() {
        // Given
        when(userRepository.findAllByRole_Name("ANNOUNCER", AnnouncersDto.class))
                .thenReturn(List.of(new AnnouncersDto(1L, "test")));

        // When
        adService.findAllAnnouncers();

        // Then
        verify(userRepository, times(1)).findAllByRole_Name(anyString(), any());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(adRepository, chargePeriodAdRepository, mapperAd,
                walletService, paymentService);
    }

    /**
     * tests function getRandomAdd
     */
    @Test
    void getRandomAdd_shouldReturnAdDtoWhenAdFound() {
        // Given
        when(adRepository.findRandomAd()).thenReturn(Optional.of(adEntity));
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        AdDto result = adService.getRandomAdd();

        // Then
        assertAll(
                () -> assertEquals(adDto, result, "Should return the expected DTO"),
                () -> assertEquals(adDto.id(), result.id(), "ID should match"),
                () -> assertEquals(adDto.content(), result.content(), "Content should match"),
                () -> verify(adRepository).findRandomAd(),
                () -> verify(mapperAd).toDto(adEntity),
                () -> verifyNoMoreInteractions(adRepository, mapperAd),
                () -> verifyNoInteractions(userRepository, chargePeriodAdRepository,
                        walletService, paymentService)
        );
    }

    @Test
    void getRandomAdd_noAdFound_shouldThrowException() {
        // Given
        when(adRepository.findRandomAd()).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class,
                () -> adService.getRandomAdd(),
                "Should throw RuntimeException when no ad found");

        verify(adRepository).findRandomAd();
        verifyNoInteractions(mapperAd);
    }

    @Test
    void getRandomAdd_shouldCallFindRandomAdExactlyOnce() {
        // Given
        when(adRepository.findRandomAd()).thenReturn(Optional.of(adEntity));
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        adService.getRandomAdd();

        // Then
        verify(adRepository, times(1)).findRandomAd();
        verifyNoMoreInteractions(adRepository);
    }

    @Test
    void getRandomAdd_shouldCallMapperWithCorrectEntity() {
        // Given
        when(adRepository.findRandomAd()).thenReturn(Optional.of(adEntity));
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        adService.getRandomAdd();

        // Then
        verify(mapperAd).toDto(adEntity);
        verifyNoMoreInteractions(mapperAd);
    }

    @Test
    void getRandomAdd_noAdFound_shouldThrowExceptionWithCorrectMessage() {
        // Given
        when(adRepository.findRandomAd()).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(RuntimeException.class,
                () -> adService.getRandomAdd());

        // Then
        assertEquals("Didnt find add", exception.getMessage(),
                "Exception message should match");
    }

    @Test
    void getRandomAdd_shouldConvertEntityToDtoCorrectly() {
        // Given
        when(adRepository.findRandomAd()).thenReturn(Optional.of(adEntity));
        when(mapperAd.toDto(adEntity)).thenReturn(adDto);

        // When
        AdDto result = adService.getRandomAdd();

        // Then
        assertAll(
                () -> assertEquals(adEntity.getId(), result.id(), "ID should be mapped"),
                () -> assertEquals(adEntity.getContent(), result.content(), "Content should be mapped"),
                () -> assertEquals(adEntity.getImageUrl(), result.imageUrl(), "Image URL should be mapped"),
                () -> assertEquals(adEntity.getVideoUrl(), result.videoUrl(), "Video URL should be mapped")
        );
    }


}