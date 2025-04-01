package org.cunoc.pdfpedia.service.monetary;

import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineReportDto;
import org.cunoc.pdfpedia.domain.dto.announcer.AdPostDto;
import org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.type.AdType;
import org.cunoc.pdfpedia.domain.type.PaymentType;
import org.cunoc.pdfpedia.repository.monetary.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    private AdEntity adEntity;
    private UserEntity advertiser;
    private ChargePeriodAdEntity chargePeriodAd;
    private PaymentEntity paymentEntity;
    private MagazineEntity magazineEntity;
    private AdReportDto adReportDto;
    private MagazineReportDto magazineReportDto;


    @BeforeEach
    void setUp(){

        advertiser = new UserEntity();
        advertiser.setId(1L);
        advertiser.setUsername("Editor");
        chargePeriodAd = new ChargePeriodAdEntity();
        chargePeriodAd.setDurationDays(2);
        chargePeriodAd.setId(1L);
        chargePeriodAd.setAdType(AdType.TEXT);
        adEntity = AdEntity
                .builder()
                .id(1l)
                .advertiser(advertiser)
                .chargePeriodAd(chargePeriodAd)
                .content("Content").imageUrl("https://example")
                .videoUrl("https://example")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        this.magazineEntity = new MagazineEntity();
        this.magazineEntity.setId(1L);
        this.magazineEntity.setTitle("Tech Magazine");
        this.magazineEntity.setEditor(advertiser);

        paymentEntity = PaymentEntity.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .paymentType(PaymentType.POST_AD)
                .ad(adEntity)
                .magazine(magazineEntity)
                .paidAt(Instant.now())
                .build();

        adReportDto = AdReportDto.builder()
                .adType(AdType.TEXT)
                .username("Editor")
                .datePay(this.paymentEntity.getPaidAt())
                .plan(2)
                .amount(new BigDecimal("100.00"))
                .build();

        magazineReportDto = MagazineReportDto.builder()
                .title("Tech Magazine")
                .editor("Editor")
                .datePay(this.paymentEntity.getPaidAt())
                .amount(new BigDecimal("100.00"))
                .build();



    }

    /**
     * tests function createPaymentPostAd
     */
    @Test
    void givenValidAmountAndAdEntity_whenCreatePaymentPostAd_thenPaymentIsCreated() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(100);
        PaymentEntity paymentEntity = PaymentEntity
                .builder()
                .amount(amount)
                .ad(adEntity)
                .paymentType(PaymentType.POST_AD)
                .build();

        // When
        paymentService.createPaymentPostAd(amount, adEntity);

        // Then
        verify(paymentRepository, times(1)).save(paymentEntity);
    }

    /**
     * tests function getSumAmountPostAdsByMount
     */
    @Test
    void givenValidUserId_whenGetSumAmountPostAdsByMount_thenReturnListOfTotalAmountPaymentByMonthDto() {
        // Given
        Long userId = 1L;
        List<TotalAmountPaymentByMonthDto> expectedAmounts = Arrays.asList(
                new TotalAmountPaymentByMonthDto("01", 500L),
                new TotalAmountPaymentByMonthDto("02", 300L)
        );

        when(paymentRepository.sumAmountAdsByMonth(userId)).thenReturn(expectedAmounts);

        // When
        List<TotalAmountPaymentByMonthDto> result = paymentService.getSumAmountPostAdsByMount(userId);

        // Then
        assertNotNull(result);
        assertEquals(expectedAmounts.size(), result.size());
        assertEquals(expectedAmounts, result);
        verify(paymentRepository, times(1)).sumAmountAdsByMonth(userId);
    }

    @Test
    void givenNoPaymentsForUser_whenGetSumAmountPostAdsByMount_thenReturnEmptyList() {
        // Given
        Long userId = 1L;
        when(paymentRepository.sumAmountAdsByMonth(userId)).thenReturn(Collections.emptyList());

        // When
        List<TotalAmountPaymentByMonthDto> result = paymentService.getSumAmountPostAdsByMount(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentRepository, times(1)).sumAmountAdsByMonth(userId);
    }

    /**
     * tests function toDtoAdReport
     */
    @Test
    void toDtoAdReport_completeEntity_shouldMapAllFieldsCorrectly() {
        // Given
        Instant testPaidAt = Instant.parse("2023-01-01T00:00:00Z");
        paymentEntity.setPaidAt(testPaidAt);
        paymentEntity.setAmount(new BigDecimal("150.50"));

        // When
        AdReportDto result = paymentService.toDtoAdReport(paymentEntity);

        // Then
        assertAll(
                () -> assertEquals(chargePeriodAd.getAdType(), result.adType(), "AdType should match"),
                () -> assertEquals(chargePeriodAd.getDurationDays(), result.plan(), "Plan duration should match"),
                () -> assertEquals(testPaidAt, result.datePay(), "Payment date should match"),
                () -> assertEquals(advertiser.getUsername(), result.username(), "Username should match"),
                () -> assertEquals(new BigDecimal("150.50"), result.amount(), "Amount should match")
        );
    }

    @Test
    void toDtoAdReport_nullFields_shouldThrowNullPointerException() {
        // Given
        paymentEntity.setAd(null);

        // When / Then
        assertThrows(NullPointerException.class,
                () -> paymentService.toDtoAdReport(paymentEntity),
                "Should throw NPE when ad is null");
    }

    @Test
    void toDtoAdReport_amountPrecision_shouldBePreserved() {
        // Given
        BigDecimal testAmount = new BigDecimal("123.4567");
        paymentEntity.setAmount(testAmount);

        // When
        AdReportDto result = paymentService.toDtoAdReport(paymentEntity);

        // Then
        assertEquals(testAmount, result.amount(), "Amount precision should be preserved");
        assertEquals(4, result.amount().scale(), "Amount scale should be preserved");
    }

    @Test
    void toDtoAdReport_instantConversion_shouldBeCorrect() {
        // Given
        Instant testInstant = Instant.parse("2023-12-31T23:59:59.999999999Z");
        paymentEntity.setPaidAt(testInstant);

        // When
        AdReportDto result = paymentService.toDtoAdReport(paymentEntity);

        // Then
        assertEquals(testInstant, result.datePay(), "Instant should be preserved exactly");
    }

    /**
     * tests function toDtoMagazineReport
     */
    @Test
    void toDtoMagazineReport_completeEntity_shouldMapAllFieldsCorrectly() {
        // Given
        BigDecimal testAmount = new BigDecimal("199.99");
        Instant testPaidAt = Instant.parse("2023-05-15T12:00:00Z");
        paymentEntity.setAmount(testAmount);
        paymentEntity.setPaidAt(testPaidAt);

        // When
        MagazineReportDto result = paymentService.toDtoMagazineReport(paymentEntity);

        // Then
        assertAll(
                () -> assertEquals(testAmount, result.amount(), "Amount should match"),
                () -> assertEquals(testPaidAt, result.datePay(), "Payment date should match"),
                () -> assertEquals(advertiser.getUsername(), result.editor(), "Editor username should match"),
                () -> assertEquals(magazineEntity.getTitle(), result.title(), "Magazine title should match")
        );
    }

    @Test
    void toDtoMagazineReport_nullMagazine_shouldThrowNullPointerException() {
        // Given
        paymentEntity.setMagazine(null);

        // When / Then
        assertThrows(NullPointerException.class,
                () -> paymentService.toDtoMagazineReport(paymentEntity),
                "Should throw NPE when magazine is null");
    }

    @Test
    void toDtoMagazineReport_amountPrecision_shouldBePreserved() {
        // Given
        BigDecimal testAmount = new BigDecimal("123.456789");
        paymentEntity.setAmount(testAmount);

        // When
        MagazineReportDto result = paymentService.toDtoMagazineReport(paymentEntity);

        // Then
        assertEquals(testAmount, result.amount(), "Amount precision should be preserved");
        assertEquals(6, result.amount().scale(), "Amount scale should be preserved");
    }

    @Test
    void toDtoMagazineReport_emptyTitle_shouldMapCorrectly() {
        // Given
        magazineEntity.setTitle("");

        // When
        MagazineReportDto result = paymentService.toDtoMagazineReport(paymentEntity);

        // Then
        assertEquals("", result.title(), "Should handle empty title");
    }

    @Test
    void toDtoMagazineReport_shouldUseBuilderPatternCorrectly() {
        // When
        MagazineReportDto result = paymentService.toDtoMagazineReport(paymentEntity);

        // Then
        assertNotNull(result, "Resulting DTO should not be null");
        assertEquals(MagazineReportDto.class, result.getClass(), "Should return MagazineReportDto instance");
    }

    /**
     * tests function getPaymentToPostAdBetween
     */
    @Test
    void getPaymentToPostAdBetween_withDateRange_shouldReturnFilteredPayments() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(paymentRepository.findByPaymentTypeAndDateRange(PaymentType.POST_AD, startInstant, endInstant))
                .thenReturn(List.of(paymentEntity));
       // when(paymentService.toDtoAdReport(paymentEntity)).thenReturn(adReportDto);

        // When
        List<AdReportDto> result = paymentService.getPaymentToPostAdBetween(startDate, endDate);

        // Then
        assertAll(
                () -> assertEquals(1, result.size(), "Should return one payment"),
                () -> assertEquals(adReportDto, result.get(0), "DTO should match")
        );
    }

    @Test
    void getPaymentToPostAdBetween_nullDates_shouldReturnAllPayments() {
        // Given
        when(paymentRepository.findByPaymentType(PaymentType.POST_AD))
                .thenReturn(List.of(paymentEntity));

        // When
        List<AdReportDto> result = paymentService.getPaymentToPostAdBetween(null, null);

        // Then
        assertAll(
                () -> assertEquals(1, result.size(), "Should return one payment"),
                () -> assertEquals(adReportDto, result.get(0), "DTO should match")
        );
    }

    @Test
    void getPaymentToPostAdBetween_oneNullDate_shouldReturnAllPayments() {
        // Given
        when(paymentRepository.findByPaymentType(PaymentType.POST_AD))
                .thenReturn(List.of(paymentEntity));

        // When
        List<AdReportDto> result1 = paymentService.getPaymentToPostAdBetween(null, LocalDate.now());
        List<AdReportDto> result2 = paymentService.getPaymentToPostAdBetween(LocalDate.now(), null);

        // Then
        assertAll(
                () -> assertEquals(1, result1.size(), "Should return one payment when startDate is null"),
                () -> assertEquals(1, result2.size(), "Should return one payment when endDate is null"),
                () -> verify(paymentRepository, times(2)).findByPaymentType(PaymentType.POST_AD)
        );
    }

    @Test
    void getPaymentToPostAdBetween_emptyResult_shouldReturnEmptyList() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(paymentRepository.findByPaymentTypeAndDateRange(PaymentType.POST_AD, startInstant, endInstant))
                .thenReturn(Collections.emptyList());

        // When
        List<AdReportDto> result = paymentService.getPaymentToPostAdBetween(startDate, endDate);

        // Then
        assertTrue(result.isEmpty(), "Should return empty list");
    }

    @Test
    void getPaymentToPostAdBetween_shouldConvertDatesToInstantCorrectly() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Instant expectedStart = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant expectedEnd = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(paymentRepository.findByPaymentTypeAndDateRange(PaymentType.POST_AD, expectedStart, expectedEnd))
                .thenReturn(List.of(paymentEntity));

        // When
        paymentService.getPaymentToPostAdBetween(startDate, endDate);

        // Then
        verify(paymentRepository).findByPaymentTypeAndDateRange(PaymentType.POST_AD, expectedStart, expectedEnd);
    }


    @Test
    void getPaymentToPostAdBetween_shouldOnlyQueryForPostAdType() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        when(paymentRepository.findByPaymentTypeAndDateRange(eq(PaymentType.POST_AD), any(), any()))
                .thenReturn(List.of(paymentEntity));

        // When
        paymentService.getPaymentToPostAdBetween(startDate, endDate);

        // Then
        verify(paymentRepository).findByPaymentTypeAndDateRange(eq(PaymentType.POST_AD), any(), any());
    }


    /**
     * tests function getPaymentToBlockAdMagazineBetween
     */
    @Test
    void getPaymentToBlockAdMagazineBetween_withDateRange_shouldReturnFilteredPayments() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(paymentRepository.findByPaymentTypeMagazineAndDateRange(PaymentType.BLOCK_ADS, startInstant, endInstant))
                .thenReturn(List.of(paymentEntity));

        // When
        List<MagazineReportDto> result = paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate);

        // Then
        assertAll(
                () -> assertEquals(1, result.size(), "Should return one payment"),
                () -> assertEquals(magazineReportDto, result.get(0), "DTO should match")
        );
    }

    @Test
    void getPaymentToBlockAdMagazineBetween_nullDates_shouldReturnAllPayments() {
        // Given
        when(paymentRepository.findByPaymentTypeMagazine(PaymentType.BLOCK_ADS))
                .thenReturn(List.of(paymentEntity));

        // When
        List<MagazineReportDto> result = paymentService.getPaymentToBlockAdMagazineBetween(null, null);

        // Then
        assertAll(
                () -> assertEquals(1, result.size(), "Should return one payment"),
                () -> assertEquals(magazineReportDto, result.get(0), "DTO should match")
        );
    }

    @Test
    void getPaymentToBlockAdMagazineBetween_oneNullDate_shouldReturnAllPayments() {
        // Given
        when(paymentRepository.findByPaymentTypeMagazine(PaymentType.BLOCK_ADS))
                .thenReturn(List.of(paymentEntity));

        // When
        List<MagazineReportDto> result1 = paymentService.getPaymentToBlockAdMagazineBetween(null, LocalDate.now());
        List<MagazineReportDto> result2 = paymentService.getPaymentToBlockAdMagazineBetween(LocalDate.now(), null);

        // Then
        assertAll(
                () -> assertEquals(1, result1.size(), "Should return payments when startDate is null"),
                () -> assertEquals(1, result2.size(), "Should return payments when endDate is null"),
                () -> verify(paymentRepository, times(2)).findByPaymentTypeMagazine(PaymentType.BLOCK_ADS)
        );
    }

    @Test
    void getPaymentToBlockAdMagazineBetween_emptyResult_shouldReturnEmptyList() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        when(paymentRepository.findByPaymentTypeMagazineAndDateRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // When
        List<MagazineReportDto> result = paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate);

        // Then
        assertTrue(result.isEmpty(), "Should return empty list");
    }

    @Test
    void getPaymentToBlockAdMagazineBetween_shouldConvertDatesToInstantCorrectly() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Instant expectedStart = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant expectedEnd = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(paymentRepository.findByPaymentTypeMagazineAndDateRange(PaymentType.BLOCK_ADS, expectedStart, expectedEnd))
                .thenReturn(List.of(paymentEntity));

        // When
        paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate);

        // Then
        verify(paymentRepository).findByPaymentTypeMagazineAndDateRange(PaymentType.BLOCK_ADS, expectedStart, expectedEnd);
    }

    @Test
    void getPaymentToBlockAdMagazineBetween_shouldOnlyQueryForBlockAdsType() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        when(paymentRepository.findByPaymentTypeMagazineAndDateRange(eq(PaymentType.BLOCK_ADS), any(), any()))
                .thenReturn(List.of(paymentEntity));

        // When
        paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate);

        // Then
        verify(paymentRepository).findByPaymentTypeMagazineAndDateRange(eq(PaymentType.BLOCK_ADS), any(), any());
    }

    /**
     * tests function getPaymentToPostAdByTypeAndBetween
     */
    @Test
    void getPaymentToPostAdByTypeAndBetween_withDateRangeAndType_shouldReturnFilteredPayments() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        int type = 1; // TEXT_IMAGE
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(paymentRepository.findByPaymentTypeAndAdTypeAndDateRange(
                PaymentType.POST_AD, AdType.TEXT_IMAGE, startInstant, endInstant))
                .thenReturn(List.of(paymentEntity));

        // When
        List<AdReportDto> result = paymentService.getPaymentToPostAdByTypeAndBetween(startDate, endDate, type);

        // Then
        assertAll(
                () -> assertEquals(1, result.size(), "Should return one payment"),
                () -> assertEquals(adReportDto, result.get(0), "DTO should match"),
                () -> verify(paymentRepository).findByPaymentTypeAndAdTypeAndDateRange(
                        PaymentType.POST_AD, AdType.TEXT_IMAGE, startInstant, endInstant),
                () -> verifyNoMoreInteractions(paymentRepository)
        );
    }

    @Test
    void getPaymentToPostAdByTypeAndBetween_nullDatesWithType_shouldReturnAllPaymentsOfType() {
        // Given
        int type = 2; // VIDEO
        when(paymentRepository.findByPaymentTypeAndAdType(PaymentType.POST_AD, AdType.VIDEO))
                .thenReturn(List.of(paymentEntity));

        // When
        List<AdReportDto> result = paymentService.getPaymentToPostAdByTypeAndBetween(null, null, type);

        // Then
        assertAll(
                () -> assertEquals(1, result.size(), "Should return one payment"),
                () -> assertEquals(adReportDto, result.get(0), "DTO should match"),
                () -> verify(paymentRepository).findByPaymentTypeAndAdType(PaymentType.POST_AD, AdType.VIDEO),
                () -> verifyNoMoreInteractions(paymentRepository)
        );
    }

    @Test
    void getPaymentToPostAdByTypeAndBetween_shouldHandleAllTypeCases() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        // Test TEXT_IMAGE (1)
        when(paymentRepository.findByPaymentTypeAndAdTypeAndDateRange(
                eq(PaymentType.POST_AD), eq(AdType.TEXT_IMAGE), any(), any()))
                .thenReturn(List.of(paymentEntity));

        // Test VIDEO (2)
        when(paymentRepository.findByPaymentTypeAndAdTypeAndDateRange(
                eq(PaymentType.POST_AD), eq(AdType.VIDEO), any(), any()))
                .thenReturn(List.of(paymentEntity));

        // Test TEXT (3)
        when(paymentRepository.findByPaymentTypeAndAdTypeAndDateRange(
                eq(PaymentType.POST_AD), eq(AdType.TEXT), any(), any()))
                .thenReturn(List.of(paymentEntity));

        // When
        List<AdReportDto> result1 = paymentService.getPaymentToPostAdByTypeAndBetween(startDate, endDate, 1);
        List<AdReportDto> result2 = paymentService.getPaymentToPostAdByTypeAndBetween(startDate, endDate, 2);
        List<AdReportDto> result3 = paymentService.getPaymentToPostAdByTypeAndBetween(startDate, endDate, 3);

        // Then
        assertAll(
                () -> assertEquals(1, result1.size(), "Should return TEXT_IMAGE payments"),
                () -> assertEquals(1, result2.size(), "Should return VIDEO payments"),
                () -> assertEquals(1, result3.size(), "Should return TEXT payments")
        );
    }

    @Test
    void getTypeFilter_type1_shouldReturnTextImage() {
        // Given
        Integer type = 1;

        // When
        AdType result = paymentService.getTypeFilter(type);

        // Then
        assertEquals(AdType.TEXT_IMAGE, result, "Should return TEXT_IMAGE for type 1");
    }

    @Test
    void getTypeFilter_type2_shouldReturnVideo() {
        // Given
        Integer type = 2;

        // When
        AdType result = paymentService.getTypeFilter(type);

        // Then
        assertEquals(AdType.VIDEO, result, "Should return VIDEO for type 2");
    }

    @Test
    void getTypeFilter_type3_shouldReturnText() {
        // Given
        Integer type = 3;

        // When
        AdType result = paymentService.getTypeFilter(type);

        // Then
        assertEquals(AdType.TEXT, result, "Should return TEXT for type 3");
    }

    @Test
    void getTypeFilter_type0_shouldReturnNull() {
        // Given
        Integer type = 0;

        // When
        AdType result = paymentService.getTypeFilter(type);

        // Then
        assertNull(result, "Should return null for type 0");
    }

    @Test
    void getTypeFilter_type4_shouldReturnNull() {
        // Given
        Integer type = 4;

        // When
        AdType result = paymentService.getTypeFilter(type);

        // Then
        assertNull(result, "Should return null for type 4");
    }

    @Test
    void getTypeFilter_negativeType_shouldReturnNull() {
        // Given
        Integer type = -1;

        // When
        AdType result = paymentService.getTypeFilter(type);

        // Then
        assertNull(result, "Should return null for negative type");
    }


    @Test
    void getTypeFilter_boundaryValues_shouldHandleCorrectly() {
        // Given
        Integer lowerBoundary = 1;
        Integer upperBoundary = 3;

        // When
        AdType lowerResult = paymentService.getTypeFilter(lowerBoundary);
        AdType upperResult = paymentService.getTypeFilter(upperBoundary);

        // Then
        assertAll(
                () -> assertEquals(AdType.TEXT_IMAGE, lowerResult, "Should handle lower boundary"),
                () -> assertEquals(AdType.TEXT, upperResult, "Should handle upper boundary")
        );
    }


}