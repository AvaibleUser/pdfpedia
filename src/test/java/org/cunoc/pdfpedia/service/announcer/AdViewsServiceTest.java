package org.cunoc.pdfpedia.service.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.AdViewsEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.domain.utils.MapperAd;
import org.cunoc.pdfpedia.repository.announcer.AdRepository;
import org.cunoc.pdfpedia.repository.announcer.AdViewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdViewsServiceTest {

    @InjectMocks
    private AdViewsService adViewsService;

    @Mock
    private AdViewsRepository adViewsRepository;
    @Mock
    private AdRepository adRepository;
    @Mock
    private MapperAd mapperAd;

    private AdViewCreateDto adViewCreateDto;
    private AdEntity adEntity;
    private AdViewsEntity adViewsEntity;
    private AdViewsEntity adViewsEntity2;
    private AdViewReportDto adViewReportDto;
    private Set<AdViewsEntity> adViewsSet = new HashSet<>();

    @BeforeEach
    void setUp(){
        adViewCreateDto = new AdViewCreateDto(1L, "https://example.com");

        adEntity = AdEntity.builder()
                .id(1L)
                .advertiser(new UserEntity())
                .chargePeriodAd(new ChargePeriodAdEntity())
                .content("Content")
                .imageUrl("https://example")
                .videoUrl("https://example")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .viewAds(adViewsSet)
                .build();

        adViewsEntity2 = AdViewsEntity.builder()
                .urlView("https://example.com")
                .ad(adEntity)
                .createdAt(Instant.now())
                .build();

        adViewsSet.add(adViewsEntity2);

        adViewsEntity = AdViewsEntity.builder()
                .urlView("https://example.com")
                .ad(adEntity)
                .build();

        adViewReportDto = AdViewReportDto.builder()
                .adDto(new AdDto(1L, 1L, "Content", "https://example", "https://example", null, LocalDateTime.now().plusDays(7), true, null))
                .viewsAdDto(List.of(new ViewAdDto("https://example.com", null)))
                .build();

    }

    /**
     * tests function create
     */
    @Test
    void givenValidAdViewCreateDto_whenCreate_thenAdViewIsCreated() {
        // Given
        when(adRepository.findById(adViewCreateDto.adId())).thenReturn(Optional.of(adEntity));
        when(adViewsRepository.save(any(AdViewsEntity.class))).thenReturn(adViewsEntity);

        // When
        adViewsService.create(adViewCreateDto);

        // Then
        verify(adRepository, times(1)).findById(adViewCreateDto.adId());
        verify(adViewsRepository, times(1)).save(any(AdViewsEntity.class));
    }

    @Test
    void givenInvalidAdId_whenCreate_thenThrowValueNotFoundException() {
        // Given
        when(adRepository.findById(adViewCreateDto.adId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValueNotFoundException.class, () -> adViewsService.create(adViewCreateDto));
        verify(adRepository, times(1)).findById(adViewCreateDto.adId());
        verify(adViewsRepository, never()).save(any(AdViewsEntity.class));
    }

    /**
     * tests function getTotalViews
     */
    @Test
    void givenValidUserId_whenGetTotalViews_thenReturnTotalViewsAdDto() {
        // Given
        Long userId = 1L;
        int totalViews = 10;

        when(adViewsRepository.countByAd_Advertiser_Id(userId)).thenReturn(totalViews);

        // When
        TotalViewsAdDto result = adViewsService.getTotalViews(userId);

        // Then
        assertNotNull(result);
        assertEquals(totalViews, result.total());
        verify(adViewsRepository, times(1)).countByAd_Advertiser_Id(userId);
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

        when(adViewsRepository.countViewsAdsByMonth(userId)).thenReturn(expectedPostAdMounts);

        // When
        List<PostAdMount> result = adViewsService.getPostMount(userId);

        // Then
        assertNotNull(result);
        assertEquals(expectedPostAdMounts.size(), result.size());
        assertEquals(expectedPostAdMounts, result);
        verify(adViewsRepository, times(1)).countViewsAdsByMonth(userId);
    }

    /**
     * tests function getReportViewsAll
     */
    @Test
    void givenValidUserId_whenGetReportViewsAll_thenReturnListOfAdViewReportDto() {
        // Given
        Long userId = 1L;
        List<AdEntity> ads = Arrays.asList(adEntity);

        when(adRepository.findByAdvertiser_Id(userId)).thenReturn(ads);
        when(mapperAd.adViewsDto(adEntity)).thenReturn(adViewReportDto);

        // When
        List<AdViewReportDto> result = adViewsService.getReportViewsAll(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(adViewReportDto, result.get(0));
        verify(adRepository, times(1)).findByAdvertiser_Id(userId);
        verify(mapperAd, times(1)).adViewsDto(adEntity);
    }

    /**
     * tests function isWithinRange
     */
    @Test
    void givenCreatedAtWithinRange_whenIsWithinRange_thenReturnTrue() {
        // Given
        Instant createdAt = Instant.now();
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        // When
        boolean result = adViewsService.isWithinRange(createdAt, startDate, endDate);

        // Then
        assertTrue(result);
    }

    @Test
    void givenCreatedAtBeforeStartDate_whenIsWithinRange_thenReturnFalse() {
        // Given
        Instant createdAt = Instant.now().minusSeconds(86400); // 1 día antes
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        // When
        boolean result = adViewsService.isWithinRange(createdAt, startDate, endDate);

        // Then
        assertFalse(result);
    }

    @Test
    void givenCreatedAtAfterEndDate_whenIsWithinRange_thenReturnFalse() {
        // Given
        Instant createdAt = Instant.now().plusSeconds(86400); // 1 día después
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();

        // When
        boolean result = adViewsService.isWithinRange(createdAt, startDate, endDate);

        // Then
        assertFalse(result);
    }

    @Test
    void givenNullStartDate_whenIsWithinRange_thenReturnTrueIfBeforeEndDate() {
        // Given
        Instant createdAt = Instant.now();
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now().plusDays(1);

        // When
        boolean result = adViewsService.isWithinRange(createdAt, startDate, endDate);

        // Then
        assertTrue(result);
    }

    @Test
    void givenNullEndDate_whenIsWithinRange_thenReturnTrueIfAfterStartDate() {
        // Given
        Instant createdAt = Instant.now();
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = null;

        // When
        boolean result = adViewsService.isWithinRange(createdAt, startDate, endDate);

        // Then
        assertTrue(result);
    }

    @Test
    void givenNullStartDateAndEndDate_whenIsWithinRange_thenReturnTrue() {
        // Given
        Instant createdAt = Instant.now();
        LocalDate startDate = null;
        LocalDate endDate = null;

        // When
        boolean result = adViewsService.isWithinRange(createdAt, startDate, endDate);

        // Then
        assertTrue(result);
    }

    /**
     * tests function getReportViewsFilterDate
     */
    @Test
    void givenValidDatesAndUserId_whenGetReportViewsFilterDate_thenReturnFilteredAdViewReportDtoList() {
        // Given
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(adRepository.findByAdvertiser_Id(userId)).thenReturn(List.of(adEntity));
        when(mapperAd.toDto(adEntity)).thenReturn(adViewReportDto.adDto());
        when(mapperAd.toDto(adViewsEntity2)).thenReturn(adViewReportDto.viewsAdDto().getFirst());

        // When
        List<AdViewReportDto> result = adViewsService.getReportViewsFilterDate(startDate, endDate, userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(adViewReportDto, result.get(0));
        verify(adRepository, times(1)).findByAdvertiser_Id(userId);
        verify(mapperAd, times(1)).toDto(adEntity);
        verify(mapperAd, times(1)).toDto(adViewsEntity2);
    }

    @Test
    void givenNoAdsForUser_whenGetReportViewsFilterDate_thenReturnEmptyList() {
        // Given
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(adRepository.findByAdvertiser_Id(userId)).thenReturn(Collections.emptyList());

        // When
        List<AdViewReportDto> result = adViewsService.getReportViewsFilterDate(startDate, endDate, userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(adRepository, times(1)).findByAdvertiser_Id(userId);
    }

    @Test
    void givenNullDates_whenGetReportViewsFilterDate_thenReturnAllAdViewReportDtoList() {
        // Given
        Long userId = 1L;
        LocalDate startDate = null;
        LocalDate endDate = null;

        when(adRepository.findByAdvertiser_Id(userId)).thenReturn(List.of(adEntity));
        when(mapperAd.toDto(adEntity)).thenReturn(adViewReportDto.adDto());
        when(mapperAd.toDto(adViewsEntity2)).thenReturn(adViewReportDto.viewsAdDto().getFirst());

        // When
        List<AdViewReportDto> result = adViewsService.getReportViewsFilterDate(startDate, endDate, userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(adViewReportDto, result.get(0));
        verify(adRepository, times(1)).findByAdvertiser_Id(userId);
        verify(mapperAd, times(1)).toDto(adEntity);
        verify(mapperAd, times(1)).toDto(adViewsEntity2);
    }

    /**
     * tests function getReportViews
     */
    @Test
    void givenNullDates_whenGetReportViews_thenCallGetReportViewsAll() {
        // Given
        Long userId = 1L;
        LocalDate startDate = null;
        LocalDate endDate = null;

        when(adRepository.findByAdvertiser_Id(userId)).thenReturn(List.of(adEntity));
        when(mapperAd.adViewsDto(adEntity)).thenReturn(adViewReportDto);

        // When
        List<AdViewReportDto> result = adViewsService.getReportViews(startDate, endDate, userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(adViewReportDto, result.get(0));
        verify(adRepository, times(1)).findByAdvertiser_Id(userId);
        verify(mapperAd, times(1)).adViewsDto(adEntity);
    }

    @Test
    void givenValidDates_whenGetReportViews_thenCallGetReportViewsFilterDate() {
        // Given
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(adRepository.findByAdvertiser_Id(userId)).thenReturn(List.of(adEntity));
        when(mapperAd.toDto(adEntity)).thenReturn(adViewReportDto.adDto());
        when(mapperAd.toDto(adViewsEntity2)).thenReturn(adViewReportDto.viewsAdDto().get(0));

        // When
        List<AdViewReportDto> result = adViewsService.getReportViews(startDate, endDate, userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(adViewReportDto, result.get(0));
        verify(adRepository, times(1)).findByAdvertiser_Id(userId);
        verify(mapperAd, times(1)).toDto(adEntity);
        verify(mapperAd, times(1)).toDto(adViewsEntity2);
    }

    @Test
    void givenNoAdsForUser_whenGetReportViews_thenReturnEmptyList() {
        // Given
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(adRepository.findByAdvertiser_Id(userId)).thenReturn(Collections.emptyList());

        // When
        List<AdViewReportDto> result = adViewsService.getReportViews(startDate, endDate, userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(adRepository, times(1)).findByAdvertiser_Id(userId);
    }

}