package org.cunoc.pdfpedia.service.admin;

import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.EarningsReport;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineCostTotalDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.AdReportEmailDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.PaymentPostAdPerAnnouncerDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.TotalReportPaymentPostAdByAnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.postAd.PostAdReportTotal;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.MagazineCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.MagazineProjectionCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.ReportMagazineCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.MagazineProjectionDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.MagazineSubscriptions;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.ReportTopMagazineSubscriptions;
import org.cunoc.pdfpedia.domain.dto.dashboard.CountRegisterByRolDto;
import org.cunoc.pdfpedia.domain.type.AdType;
import org.cunoc.pdfpedia.repository.interaction.CommentRepository;
import org.cunoc.pdfpedia.repository.interaction.SubscriptionRepository;
import org.cunoc.pdfpedia.repository.monetary.PaymentRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.cunoc.pdfpedia.service.monetary.IPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IPaymentService paymentService;

    @Mock
    private IAdminService adminService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ReportService reportService;


    private LocalDate startDate;
    private LocalDate endDate;
    private Integer adType;
    private  Instant startInstant;
    private Instant endInstant ;
    private final Long userId = 1L;
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 12, 31);
        startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        endInstant = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        adType = 1;
    }

    /**
     * tests Function findCountRegisterByRol
     */
    @Test
    void givenNullDates_whenFindCountRegisterByRol_thenReturnCountsWithoutDateFiltering() {
        // Given
        LocalDate startDate = null;
        LocalDate endDate = null;

        long announcerCount = 5L;
        long editorCount = 3L;
        long userCount = 10L;

        when(userRepository.countByRole_Name("ANNOUNCER")).thenReturn(announcerCount);
        when(userRepository.countByRole_Name("EDITOR")).thenReturn(editorCount);
        when(userRepository.countByRole_Name("USER")).thenReturn(userCount);

        // When
        List<CountRegisterByRolDto> result = reportService.findCountRegisterByRol(startDate, endDate);

        // Then

        assertThat(result.get(0).typeUser()).isEqualTo("ANNOUNCER");
        assertThat(result.get(0).count()).isEqualTo(announcerCount);

        assertThat(result.get(1).typeUser()).isEqualTo("EDITOR");
        assertThat(result.get(1).count()).isEqualTo(editorCount);

        assertThat(result.get(2).typeUser()).isEqualTo("USER");
        assertThat(result.get(2).count()).isEqualTo(userCount);

        verify(userRepository).countByRole_Name("ANNOUNCER");
        verify(userRepository).countByRole_Name("EDITOR");
        verify(userRepository).countByRole_Name("USER");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenValidDates_whenFindCountRegisterByRol_thenReturnCountsFilteredByDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        long announcerCount = 2L;
        long editorCount = 4L;
        long userCount = 8L;

        when(userRepository.countByRole_NameAndCreatedAtBetween("ANNOUNCER", startInstant, endInstant))
                .thenReturn(announcerCount);
        when(userRepository.countByRole_NameAndCreatedAtBetween("EDITOR", startInstant, endInstant))
                .thenReturn(editorCount);
        when(userRepository.countByRole_NameAndCreatedAtBetween("USER", startInstant, endInstant))
                .thenReturn(userCount);

        // When
        List<CountRegisterByRolDto> result = reportService.findCountRegisterByRol(startDate, endDate);

        // Then

        assertThat(result.get(0).typeUser()).isEqualTo("ANNOUNCER");
        assertThat(result.get(0).count()).isEqualTo(announcerCount);

        assertThat(result.get(1).typeUser()).isEqualTo("EDITOR");
        assertThat(result.get(1).count()).isEqualTo(editorCount);

        assertThat(result.get(2).typeUser()).isEqualTo("USER");
        assertThat(result.get(2).count()).isEqualTo(userCount);

        verify(userRepository).countByRole_NameAndCreatedAtBetween("ANNOUNCER", startInstant, endInstant);
        verify(userRepository).countByRole_NameAndCreatedAtBetween("EDITOR", startInstant, endInstant);
        verify(userRepository).countByRole_NameAndCreatedAtBetween("USER", startInstant, endInstant);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenStartDateNullAndEndDateNotNull_whenFindCountRegisterByRol_thenReturnCountsWithoutDateFiltering() {
        // Given
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        long announcerCount = 5L;
        long editorCount = 3L;
        long userCount = 10L;

        when(userRepository.countByRole_Name("ANNOUNCER")).thenReturn(announcerCount);
        when(userRepository.countByRole_Name("EDITOR")).thenReturn(editorCount);
        when(userRepository.countByRole_Name("USER")).thenReturn(userCount);

        // When
        List<CountRegisterByRolDto> result = reportService.findCountRegisterByRol(startDate, endDate);

        // Then

        verify(userRepository).countByRole_Name("ANNOUNCER");
        verify(userRepository).countByRole_Name("EDITOR");
        verify(userRepository).countByRole_Name("USER");
    }

    @Test
    void givenStartDateNotNullAndEndDateNull_whenFindCountRegisterByRol_thenReturnCountsWithoutDateFiltering() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = null;

        long announcerCount = 5L;
        long editorCount = 3L;
        long userCount = 10L;

        when(userRepository.countByRole_Name("ANNOUNCER")).thenReturn(announcerCount);
        when(userRepository.countByRole_Name("EDITOR")).thenReturn(editorCount);
        when(userRepository.countByRole_Name("USER")).thenReturn(userCount);

        // When
        List<CountRegisterByRolDto> result = reportService.findCountRegisterByRol(startDate, endDate);

        // Then

        verify(userRepository).countByRole_Name("ANNOUNCER");
        verify(userRepository).countByRole_Name("EDITOR");
        verify(userRepository).countByRole_Name("USER");
    }

    @Test
    void givenValidDatesAndZeroCounts_whenFindCountRegisterByRol_thenReturnDTOsWithZeroCounts() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        when(userRepository.countByRole_NameAndCreatedAtBetween("ANNOUNCER", startInstant, endInstant))
                .thenReturn(0L);
        when(userRepository.countByRole_NameAndCreatedAtBetween("EDITOR", startInstant, endInstant))
                .thenReturn(0L);
        when(userRepository.countByRole_NameAndCreatedAtBetween("USER", startInstant, endInstant))
                .thenReturn(0L);

        // When
        List<CountRegisterByRolDto> result = reportService.findCountRegisterByRol(startDate, endDate);

        // Then

        assertThat(result.get(0).typeUser()).isEqualTo("ANNOUNCER");
        assertThat(result.get(0).count()).isZero();

        assertThat(result.get(1).typeUser()).isEqualTo("EDITOR");
        assertThat(result.get(1).count()).isZero();

        assertThat(result.get(2).typeUser()).isEqualTo("USER");
        assertThat(result.get(2).count()).isZero();
    }

    /**
     * tests function getTotalAdPost
     */
    @Test
    void givenEmptyList_whenGetTotalAdPost_thenReturnZero() {
        // Given
        List<AdReportDto> adPostReport = Collections.emptyList();

        // When
        BigDecimal result = reportService.getTotalAdPost(adPostReport);

        // Then
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenSingleAdReport_whenGetTotalAdPost_thenReturnCorrectAmount() {
        // Given
        BigDecimal expectedAmount = BigDecimal.valueOf(100.50);
        AdReportDto adReport = new AdReportDto(
                AdType.TEXT,
                "user1",
                Instant.now(),
                30,
                expectedAmount
        );
        List<AdReportDto> adPostReport = Collections.singletonList(adReport);

        // When
        BigDecimal result = reportService.getTotalAdPost(adPostReport);

        // Then
        assertThat(result).isEqualTo(expectedAmount);
    }

    @Test
    void givenMultipleAdReports_whenGetTotalAdPost_thenReturnSumOfAmounts() {
        // Given
        BigDecimal amount1 = BigDecimal.valueOf(100.50);
        BigDecimal amount2 = BigDecimal.valueOf(200.75);
        BigDecimal amount3 = BigDecimal.valueOf(50.25);
        BigDecimal expectedTotal = amount1.add(amount2).add(amount3);

        List<AdReportDto> adPostReport = Arrays.asList(
                new AdReportDto(AdType.TEXT_IMAGE, "user1", Instant.now(), 30, amount1),
                new AdReportDto(AdType.TEXT, "user2", Instant.now(), 15, amount2),
                new AdReportDto(AdType.VIDEO, "user3", Instant.now(), 7, amount3)
        );

        // When
        BigDecimal result = reportService.getTotalAdPost(adPostReport);

        // Then
        assertThat(result).isEqualTo(expectedTotal);
    }

    @Test
    void givenListWithNullAmount_whenGetTotalAdPost_thenIgnoreNullAndSumValidAmounts() {
        // Given
        BigDecimal amount1 = BigDecimal.valueOf(100.50);
        BigDecimal amount2 = BigDecimal.valueOf(200.75);
        BigDecimal expectedTotal = amount1.add(amount2);

        List<AdReportDto> adPostReport = Arrays.asList(
                new AdReportDto(AdType.TEXT, "user1", Instant.now(), 30, amount1),
                new AdReportDto(AdType.TEXT_IMAGE, "user2", Instant.now(), 15,  BigDecimal.valueOf(0)),
                new AdReportDto(AdType.VIDEO, "user3", Instant.now(), 7, amount2)
        );

        // When
        BigDecimal result = reportService.getTotalAdPost(adPostReport);

        // Then
        assertThat(result).isEqualTo(expectedTotal);
    }

    @Test
    void givenListWithAllNullAmounts_whenGetTotalAdPost_thenReturnZero() {
        // Given
        List<AdReportDto> adPostReport = Arrays.asList(
                new AdReportDto(AdType.TEXT, "user1", Instant.now(), 30,  BigDecimal.valueOf(0)),
                new AdReportDto(AdType.TEXT_IMAGE, "user2", Instant.now(), 15,  BigDecimal.valueOf(0)),
                new AdReportDto(AdType.VIDEO, "user3", Instant.now(), 7,  BigDecimal.valueOf(0))
        );

        // When
        BigDecimal result = reportService.getTotalAdPost(adPostReport);

        // Then
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenLargeListOfAdReports_whenGetTotalAdPost_thenReturnCorrectSum() {
        // Given
        int count = 1000;
        BigDecimal unitAmount = BigDecimal.valueOf(10);
        BigDecimal expectedTotal = unitAmount.multiply(BigDecimal.valueOf(count));

        List<AdReportDto> adPostReport = IntStream.range(0, count)
                .mapToObj(i -> new AdReportDto(
                        AdType.TEXT_IMAGE,
                        "user" + i,
                        Instant.now(),
                        30,
                        unitAmount))
                .collect(Collectors.toList());

        // When
        BigDecimal result = reportService.getTotalAdPost(adPostReport);

        // Then
        assertThat(result).isEqualTo(expectedTotal);
    }

    /**
     * tests function getTotalAdPostEmail
     */
    @Test
    void givenEmptyList_whenGetTotalAdPostEmail_thenReturnZero() {
        // Given
        List<AdReportEmailDto> adPostReport = Collections.emptyList();

        // When
        BigDecimal result = reportService.getTotalAdPostEmail(adPostReport);

        // Then
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenSingleAdReport_whenGetTotalAdPostEmail_thenReturnCorrectAmount() {
        // Given
        BigDecimal expectedAmount = BigDecimal.valueOf(100.50);
        AdReportEmailDto adReport = new AdReportEmailDto(
                AdType.TEXT,
                "user1",
                Instant.now(),
                30,
                expectedAmount,
                "elvis@gamil.com"
        );
        List<AdReportEmailDto> adPostReport = Collections.singletonList(adReport);

        // When
        BigDecimal result = reportService.getTotalAdPostEmail(adPostReport);

        // Then
        assertThat(result).isEqualTo(expectedAmount);
    }

    /**
     * tests function getTotalBlockAd
     */
    @Test
    void givenEmptyList_whenGetTotalBlockAd_thenReturnZero() {
        // Given
        List<MagazineReportDto> adPostReport = Collections.emptyList();

        // When
        BigDecimal result = reportService.getTotalBlockAd(adPostReport);

        // Then
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenSingleAdReport_whenGetTotalBlockAd_thenReturnCorrectAmount() {
        // Given
        BigDecimal expectedAmount = BigDecimal.valueOf(100.50);
        MagazineReportDto adReport = new MagazineReportDto(
                "title1",
                "Editor1",
                Instant.now(),
                expectedAmount
        );
        List<MagazineReportDto> adPostReport = Collections.singletonList(adReport);

        // When
        BigDecimal result = reportService.getTotalBlockAd(adPostReport);

        // Then
        assertThat(result).isEqualTo(expectedAmount);
    }

    /**
     * tests function getCostTotal
     */
    @Test
    void givenEmptyList_whenGetCostTotal_thenReturnZero() {
        // Given
        List<MagazineCostTotalDto> adPostReport = Collections.emptyList();

        // When
        BigDecimal result = reportService.getCostTotal(adPostReport);

        // Then
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenSingleAdReport_whenGetCostTotal_thenReturnCorrectAmount() {
        // Given
        BigDecimal expectedAmount = BigDecimal.valueOf(100.50);
        MagazineCostTotalDto adReport = new MagazineCostTotalDto(
                "title1",
                expectedAmount,
                Instant.now(),
                "username",
                expectedAmount
        );
        List<MagazineCostTotalDto> adPostReport = Collections.singletonList(adReport);

        // When
        BigDecimal result = reportService.getCostTotal(adPostReport);

        // Then
        assertThat(result).isEqualTo(expectedAmount);
    }


    /**
     * tests functions getTotalReportEarnings
     */
    @Test
    void givenValidDates_whenGetTotalReportEarnings_thenReturnCompleteEarningsReport() {
        // Given
        BigDecimal adPostAmount1 = BigDecimal.valueOf(100);
        BigDecimal adPostAmount2 = BigDecimal.valueOf(200);
        List<AdReportDto> adPostReport = List.of(
                new AdReportDto(AdType.TEXT_IMAGE, "user1", Instant.now(), 30, adPostAmount1),
                new AdReportDto(AdType.TEXT, "user2", Instant.now(), 15, adPostAmount2)
        );

        BigDecimal blockAdAmount1 = BigDecimal.valueOf(150);
        BigDecimal blockAdAmount2 = BigDecimal.valueOf(250);
        List<MagazineReportDto> blockAdReport = List.of(
                new MagazineReportDto("Magazine1", "editor1", Instant.now(), blockAdAmount1),
                new MagazineReportDto("Magazine2", "editor2", Instant.now(), blockAdAmount2)
        );

        BigDecimal costTotal1 = BigDecimal.valueOf(50);
        BigDecimal costTotal2 = BigDecimal.valueOf(75);
        List<MagazineCostTotalDto> costTotalReport = List.of(
                new MagazineCostTotalDto("Magazine1", BigDecimal.valueOf(5), Instant.now(), "editor1", costTotal1),
                new MagazineCostTotalDto("Magazine2", BigDecimal.valueOf(7.5), Instant.now(), "editor2", costTotal2)
        );

        BigDecimal expectedTotalAdPost = adPostAmount1.add(adPostAmount2);
        BigDecimal expectedTotalBlockAd = blockAdAmount1.add(blockAdAmount2);
        BigDecimal expectedTotalCost = costTotal1.add(costTotal2);
        BigDecimal expectedTotalIncome = expectedTotalAdPost.add(expectedTotalBlockAd);
        BigDecimal expectedTotalEarnings = expectedTotalIncome.subtract(expectedTotalCost);

        when(paymentService.getPaymentToPostAdBetween(startDate, endDate)).thenReturn(adPostReport);
        when(paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate)).thenReturn(blockAdReport);
        when(adminService.getAllCostTotalMagazines(startDate, endDate)).thenReturn(costTotalReport);

        // When
        EarningsReport result = reportService.getTotalReportEarnings(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.adReportDto().size()).isEqualTo(2);
        assertThat(result.magazineReportDto().size()).isEqualTo(2);
        assertThat(result.magazineCostTotalDto().size()).isEqualTo(2);

        assertThat(result.totalAdPost()).isEqualByComparingTo(expectedTotalAdPost);
        assertThat(result.totalAdBlocks()).isEqualByComparingTo(expectedTotalBlockAd);
        assertThat(result.totalCostPerDay()).isEqualByComparingTo(expectedTotalCost);
        assertThat(result.totalIncome()).isEqualByComparingTo(expectedTotalIncome);
        assertThat(result.totalEarnings()).isEqualByComparingTo(expectedTotalEarnings);

        verify(paymentService).getPaymentToPostAdBetween(startDate, endDate);
        verify(paymentService).getPaymentToBlockAdMagazineBetween(startDate, endDate);
        verify(adminService).getAllCostTotalMagazines(startDate, endDate);
    }

    @Test
    void givenEmptyReports_whenGetTotalReportEarnings_thenReturnReportWithZeros() {
        // Given
        when(paymentService.getPaymentToPostAdBetween(startDate, endDate)).thenReturn(Collections.emptyList());
        when(paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate)).thenReturn(Collections.emptyList());
        when(adminService.getAllCostTotalMagazines(startDate, endDate)).thenReturn(Collections.emptyList());

        // When
        EarningsReport result = reportService.getTotalReportEarnings(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.adReportDto().isEmpty()).isTrue();
        assertThat(result.magazineReportDto().isEmpty()).isTrue();
        assertThat(result.magazineCostTotalDto().isEmpty()).isTrue();

        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalAdBlocks()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalCostPerDay()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalEarnings()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenOnlyAdPostPayments_whenGetTotalReportEarnings_thenReturnCorrectEarnings() {
        // Given
        BigDecimal adPostAmount = BigDecimal.valueOf(300);
        List<AdReportDto> adPostReport = List.of(
                new AdReportDto(AdType.TEXT, "user1", Instant.now(), 30, adPostAmount)
        );

        when(paymentService.getPaymentToPostAdBetween(startDate, endDate)).thenReturn(adPostReport);
        when(paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate)).thenReturn(Collections.emptyList());
        when(adminService.getAllCostTotalMagazines(startDate, endDate)).thenReturn(Collections.emptyList());

        // When
        EarningsReport result = reportService.getTotalReportEarnings(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.totalAdPost()).isEqualByComparingTo(adPostAmount);
        assertThat(result.totalAdBlocks()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalCostPerDay()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalIncome()).isEqualByComparingTo(adPostAmount);
        assertThat(result.totalEarnings()).isEqualByComparingTo(adPostAmount);
    }

    @Test
    void givenOnlyBlockAdPayments_whenGetTotalReportEarnings_thenReturnCorrectEarnings() {
        // Given
        BigDecimal blockAdAmount = BigDecimal.valueOf(500);
        List<MagazineReportDto> blockAdReport = List.of(
                new MagazineReportDto("Magazine1", "editor1", Instant.now(), blockAdAmount)
        );

        when(paymentService.getPaymentToPostAdBetween(startDate, endDate)).thenReturn(Collections.emptyList());
        when(paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate)).thenReturn(blockAdReport);
        when(adminService.getAllCostTotalMagazines(startDate, endDate)).thenReturn(Collections.emptyList());

        // When
        EarningsReport result = reportService.getTotalReportEarnings(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalAdBlocks()).isEqualByComparingTo(blockAdAmount);
        assertThat(result.totalCostPerDay()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalIncome()).isEqualByComparingTo(blockAdAmount);
        assertThat(result.totalEarnings()).isEqualByComparingTo(blockAdAmount);
    }

    @Test
    void givenOnlyMagazineCosts_whenGetTotalReportEarnings_thenReturnNegativeEarnings() {
        // Given
        BigDecimal costTotal = BigDecimal.valueOf(200);
        List<MagazineCostTotalDto> costTotalReport = List.of(
                new MagazineCostTotalDto("Magazine1", BigDecimal.valueOf(10), Instant.now(), "editor1", costTotal)
        );

        when(paymentService.getPaymentToPostAdBetween(startDate, endDate)).thenReturn(Collections.emptyList());
        when(paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate)).thenReturn(Collections.emptyList());
        when(adminService.getAllCostTotalMagazines(startDate, endDate)).thenReturn(costTotalReport);

        // When
        EarningsReport result = reportService.getTotalReportEarnings(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalAdBlocks()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalCostPerDay()).isEqualByComparingTo(costTotal);
        assertThat(result.totalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalEarnings()).isEqualByComparingTo(costTotal.negate());
    }

    @Test
    void givenMixedDataWithNegativeEarnings_whenGetTotalReportEarnings_thenReturnCorrectNegativeEarnings() {
        // Given
        BigDecimal adPostAmount = BigDecimal.valueOf(100);
        BigDecimal blockAdAmount = BigDecimal.valueOf(200);
        BigDecimal costTotal = BigDecimal.valueOf(400);
        BigDecimal expectedEarnings = adPostAmount.add(blockAdAmount).subtract(costTotal);

        when(paymentService.getPaymentToPostAdBetween(startDate, endDate))
                .thenReturn(List.of(new AdReportDto(AdType.TEXT, "user1", Instant.now(), 30, adPostAmount)));
        when(paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate))
                .thenReturn(List.of(new MagazineReportDto("Magazine1", "editor1", Instant.now(), blockAdAmount)));
        when(adminService.getAllCostTotalMagazines(startDate, endDate))
                .thenReturn(List.of(new MagazineCostTotalDto("Magazine1", BigDecimal.valueOf(10), Instant.now(), "editor1", costTotal)));

        // When
        EarningsReport result = reportService.getTotalReportEarnings(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.totalEarnings()).isEqualByComparingTo(expectedEarnings);
        assertThat(result.totalEarnings().signum()).isNegative();
    }

    /**
     * tests functions getPostAdReportTotal
     */
    @Test
    void givenValidDatesAndType_whenGetPostAdReportTotal_thenReturnCompleteReport() {
        // Given
        BigDecimal amount1 = BigDecimal.valueOf(100);
        BigDecimal amount2 = BigDecimal.valueOf(200);
        List<AdReportDto> adPostReport = List.of(
                new AdReportDto(AdType.TEXT, "user1", Instant.now(), 30, amount1),
                new AdReportDto(AdType.TEXT, "user2", Instant.now(), 15, amount2)
        );
        BigDecimal expectedTotal = amount1.add(amount2);

        when(paymentService.getPaymentToPostAdByTypeAndBetween(startDate, endDate, adType))
                .thenReturn(adPostReport);

        // When
        PostAdReportTotal result = reportService.getPostAdReportTotal(startDate, endDate, adType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.adReportDto().size()).isEqualTo(2);
        assertThat(result.totalAdPost()).isEqualByComparingTo(expectedTotal);

        verify(paymentService).getPaymentToPostAdByTypeAndBetween(startDate, endDate, adType);
    }

    @Test
    void givenNullDates_whenGetPostAdReportTotal_thenProcessWithNullDates() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(150);
        List<AdReportDto> adPostReport = List.of(
                new AdReportDto(AdType.TEXT, "user1", Instant.now(), 30, amount)
        );

        when(paymentService.getPaymentToPostAdByTypeAndBetween(null, null, adType))
                .thenReturn(adPostReport);

        // When
        PostAdReportTotal result = reportService.getPostAdReportTotal(null, null, adType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.adReportDto().size()).isEqualTo(1);
        assertThat(result.totalAdPost()).isEqualByComparingTo(amount);

        verify(paymentService).getPaymentToPostAdByTypeAndBetween(null, null, adType);
    }

    @Test
    void givenNullType_whenGetPostAdReportTotal_thenProcessWithNullType() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(250);
        List<AdReportDto> adPostReport = List.of(
                new AdReportDto(AdType.TEXT, "user1", Instant.now(), 30, amount)
        );

        when(paymentService.getPaymentToPostAdByTypeAndBetween(startDate, endDate, null))
                .thenReturn(adPostReport);

        // When
        PostAdReportTotal result = reportService.getPostAdReportTotal(startDate, endDate, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.adReportDto().size()).isEqualTo(1);
        assertThat(result.totalAdPost()).isEqualByComparingTo(amount);

        verify(paymentService).getPaymentToPostAdByTypeAndBetween(startDate, endDate, null);
    }

    @Test
    void givenEmptyReport_whenGetPostAdReportTotal_thenReturnReportWithZeroTotal() {
        // Given
        when(paymentService.getPaymentToPostAdByTypeAndBetween(startDate, endDate, adType))
                .thenReturn(Collections.emptyList());

        // When
        PostAdReportTotal result = reportService.getPostAdReportTotal(startDate, endDate, adType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.adReportDto().isEmpty()).isTrue();
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenReportWithNullAmounts_whenGetPostAdReportTotal_thenIgnoreNullAmountsInTotal() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(300);
        List<AdReportDto> adPostReport = List.of(
                new AdReportDto(AdType.TEXT, "user1", Instant.now(), 30, amount),
                new AdReportDto(AdType.TEXT_IMAGE, "user2", Instant.now(), 15, BigDecimal.valueOf(0))
        );

        when(paymentService.getPaymentToPostAdByTypeAndBetween(startDate, endDate, adType))
                .thenReturn(adPostReport);

        // When
        PostAdReportTotal result = reportService.getPostAdReportTotal(startDate, endDate, adType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.adReportDto().size()).isEqualTo(2);
        assertThat(result.totalAdPost()).isEqualByComparingTo(amount);
    }

    /**
     * tests function mapperReport
     */
    @Test
    void givenPaymentsAndAdReports_whenMapperReport_thenReturnEnrichedReportWithCorrectTotal() {
        // Given
        String username1 = "user1";
        String username2 = "user2";

        AdReportEmailDto ad1 = new AdReportEmailDto(AdType.TEXT, username1, Instant.now(), 30,
                BigDecimal.valueOf(100), "user1@example.com");
        AdReportEmailDto ad2 = new AdReportEmailDto(AdType.TEXT_IMAGE, username1, Instant.now(), 15,
                BigDecimal.valueOf(50), "user1@example.com");
        AdReportEmailDto ad3 = new AdReportEmailDto(AdType.VIDEO, username2, Instant.now(), 7,
                BigDecimal.valueOf(75), "user2@example.com");

        List<AdReportEmailDto> adReports = List.of(ad1, ad2, ad3);
        BigDecimal expectedTotal = BigDecimal.valueOf(225); // 100 + 50 + 75

        PaymentPostAdPerAnnouncerDto payment1 = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(150), new ArrayList<>(), username1);
        PaymentPostAdPerAnnouncerDto payment2 = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(75), new ArrayList<>(), username2);

        List<PaymentPostAdPerAnnouncerDto> payments = List.of(payment1, payment2);

        // When
        TotalReportPaymentPostAdByAnnouncersDto result = reportService.mapperReport(payments, adReports);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(2);
        assertThat(result.totalAdPost()).isEqualByComparingTo(expectedTotal);

        // Verify user1 ads
        PaymentPostAdPerAnnouncerDto enrichedPayment1 = result.paymentPostAdPerAnnouncerDtos().getFirst();
        assertThat(enrichedPayment1.username()).isEqualTo(username1);
        assertThat(enrichedPayment1.adReportDtos().size()).isEqualTo(2);

        // Verify user2 ads
        PaymentPostAdPerAnnouncerDto enrichedPayment2 = result.paymentPostAdPerAnnouncerDtos().get(1);
        assertThat(enrichedPayment2.username()).isEqualTo(username2);
        assertThat(enrichedPayment2.adReportDtos().size()).isEqualTo(1);
    }

    @Test
    void givenEmptyAdReports_whenMapperReport_thenReturnReportWithEmptyAdsAndZeroTotal() {
        // Given
        String username = "user1";
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(100), new ArrayList<>(), username);

        List<PaymentPostAdPerAnnouncerDto> payments = List.of(payment);
        List<AdReportEmailDto> adReports = Collections.emptyList();

        // When
        TotalReportPaymentPostAdByAnnouncersDto result = reportService.mapperReport(payments, adReports);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(1);
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);

        PaymentPostAdPerAnnouncerDto enrichedPayment = result.paymentPostAdPerAnnouncerDtos().get(0);
        assertThat(enrichedPayment.adReportDtos().isEmpty()).isTrue();
    }

    @Test
    void givenEmptyPayments_whenMapperReport_thenReturnEmptyReportWithCorrectTotal() {
        // Given
        AdReportEmailDto ad = new AdReportEmailDto(AdType.TEXT, "user1", Instant.now(), 30,
                BigDecimal.valueOf(100), "user1@example.com");

        List<PaymentPostAdPerAnnouncerDto> payments = Collections.emptyList();
        List<AdReportEmailDto> adReports = List.of(ad);

        // When
        TotalReportPaymentPostAdByAnnouncersDto result = reportService.mapperReport(payments, adReports);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().isEmpty()).isTrue();
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void givenPaymentsWithNoMatchingAds_whenMapperReport_thenReturnPaymentsWithEmptyAds() {
        // Given
        String username1 = "user1";
        String username2 = "user2";

        AdReportEmailDto ad = new AdReportEmailDto(AdType.TEXT, "user3", Instant.now(), 30,
                BigDecimal.valueOf(100), "user3@example.com");

        PaymentPostAdPerAnnouncerDto payment1 = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(150), new ArrayList<>(), username1);
        PaymentPostAdPerAnnouncerDto payment2 = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(75), new ArrayList<>(), username2);

        List<PaymentPostAdPerAnnouncerDto> payments = List.of(payment1, payment2);
        List<AdReportEmailDto> adReports = List.of(ad);

        // When
        TotalReportPaymentPostAdByAnnouncersDto result = reportService.mapperReport(payments, adReports);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(2);
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.valueOf(100));

        assertThat(result.paymentPostAdPerAnnouncerDtos().get(0).adReportDtos().isEmpty()).isTrue();
        assertThat(result.paymentPostAdPerAnnouncerDtos().get(1).adReportDtos().isEmpty()).isTrue();
    }

    /**
     * tests function getTotalPaymentsByAdvertisers
     */
    @Test
    void givenSuccessfulRepositoryCalls_whenGetTotalPaymentsByAdvertisers_thenReturnCompleteReport() {
        // Given
        String username1 = "user1";
        String username2 = "user2";

        // Mock payment data
        PaymentPostAdPerAnnouncerDto payment1 = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(150), new ArrayList<>(), username1);
        PaymentPostAdPerAnnouncerDto payment2 = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(200), new ArrayList<>(), username2);
        List<PaymentPostAdPerAnnouncerDto> payments = List.of(payment1, payment2);

        // Mock ad report data
        AdReportEmailDto ad1 = new AdReportEmailDto(AdType.TEXT, username1, Instant.now(), 30,
                BigDecimal.valueOf(100), "user1@example.com");
        AdReportEmailDto ad2 = new AdReportEmailDto(AdType.TEXT, username1, Instant.now(), 15,
                BigDecimal.valueOf(50), "user1@example.com");
        AdReportEmailDto ad3 = new AdReportEmailDto(AdType.VIDEO, username2, Instant.now(), 7,
                BigDecimal.valueOf(200), "user2@example.com");
        List<AdReportEmailDto> adReports = List.of(ad1, ad2, ad3);

        // Expected total
        BigDecimal expectedTotal = BigDecimal.valueOf(350); // 100 + 50 + 200

        when(paymentRepository.findGroupedPaymentsByPaymentType()).thenReturn(payments);
        when(paymentRepository.findAdReportsByPaymentType()).thenReturn(adReports);

        // When
        TotalReportPaymentPostAdByAnnouncersDto result = reportService.getTotalPaymentsByAdvertisers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(2);
        assertThat(result.totalAdPost()).isEqualByComparingTo(expectedTotal);

        // Verify payments were enriched with ads
        assertThat(result.paymentPostAdPerAnnouncerDtos().get(0).adReportDtos().size()).isEqualTo(2);
        assertThat(result.paymentPostAdPerAnnouncerDtos().get(1).adReportDtos().size()).isEqualTo(1);

        verify(paymentRepository).findGroupedPaymentsByPaymentType();
        verify(paymentRepository).findAdReportsByPaymentType();
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void givenEmptyPayments_whenGetTotalPaymentsByAdvertisers_thenReturnReportWithEmptyPayments() {
        // Given
        when(paymentRepository.findGroupedPaymentsByPaymentType()).thenReturn(Collections.emptyList());
        when(paymentRepository.findAdReportsByPaymentType()).thenReturn(List.of(
                new AdReportEmailDto(AdType.TEXT, "user1", Instant.now(), 30,
                        BigDecimal.valueOf(100), "user1@example.com")
        ));

        // When
        TotalReportPaymentPostAdByAnnouncersDto result = reportService.getTotalPaymentsByAdvertisers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().isEmpty()).isTrue();
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void givenEmptyAdReports_whenGetTotalPaymentsByAdvertisers_thenReturnReportWithZeroTotal() {
        // Given
        when(paymentRepository.findGroupedPaymentsByPaymentType()).thenReturn(List.of(
                new PaymentPostAdPerAnnouncerDto(BigDecimal.valueOf(150), new ArrayList<>(), "user1")
        ));
        when(paymentRepository.findAdReportsByPaymentType()).thenReturn(Collections.emptyList());

        // When
        TotalReportPaymentPostAdByAnnouncersDto result = reportService.getTotalPaymentsByAdvertisers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(1);
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    /**
     * tests function getTotalPaymentsByAdvertisers
     */

    @Test
    void givenValidDates_whenGetTotalPaymentsByAdvertisers_thenReturnFilteredReport() {
        // Given
        String username = "testUser";
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(100), new ArrayList<>(), username);
        AdReportEmailDto adReport = new AdReportEmailDto(
                AdType.TEXT, username, Instant.now(), 30, BigDecimal.valueOf(50), "test@example.com");

        when(paymentRepository.findGroupedPaymentsByPaymentTypeAndBetween(startInstant, endInstant))
                .thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentTypeAndBetween(startInstant, endInstant))
                .thenReturn(List.of(adReport));

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisers(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(1);
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.valueOf(50));

        verify(paymentRepository).findGroupedPaymentsByPaymentTypeAndBetween(startInstant, endInstant);
        verify(paymentRepository).findAdReportsByPaymentTypeAndBetween(startInstant, endInstant);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void givenNullStartDate_whenGetTotalPaymentsByAdvertisers_thenCallUnfilteredMethod() {
        // Given
        String username = "testUser";
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(100), new ArrayList<>(), username);
        AdReportEmailDto adReport = new AdReportEmailDto(
                AdType.TEXT, username, Instant.now(), 30, BigDecimal.valueOf(50), "test@example.com");

        when(paymentRepository.findGroupedPaymentsByPaymentType()).thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentType()).thenReturn(List.of(adReport));

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisers(null, endDate);

        // Then
        assertThat(result).isNotNull();
        verify(paymentRepository).findGroupedPaymentsByPaymentType();
        verify(paymentRepository).findAdReportsByPaymentType();
    }

    @Test
    void givenNullEndDate_whenGetTotalPaymentsByAdvertisers_thenCallUnfilteredMethod() {
        // Given
        String username = "testUser";
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(100), new ArrayList<>(), username);
        AdReportEmailDto adReport = new AdReportEmailDto(
                AdType.TEXT, username, Instant.now(), 30, BigDecimal.valueOf(50), "test@example.com");

        when(paymentRepository.findGroupedPaymentsByPaymentType()).thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentType()).thenReturn(List.of(adReport));

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisers(startDate, null);

        // Then
        assertThat(result).isNotNull();
        verify(paymentRepository).findGroupedPaymentsByPaymentType();
        verify(paymentRepository).findAdReportsByPaymentType();
    }

    @Test
    void givenEmptyResults_whenGetTotalPaymentsByAdvertisers_thenReturnEmptyReport() {
        // Given
        when(paymentRepository.findGroupedPaymentsByPaymentTypeAndBetween(startInstant, endInstant))
                .thenReturn(Collections.emptyList());
        when(paymentRepository.findAdReportsByPaymentTypeAndBetween(startInstant, endInstant))
                .thenReturn(Collections.emptyList());

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisers(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().isEmpty()).isTrue();
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenAdReportsWithNullAmounts_whenGetTotalPaymentsByAdvertisers_thenExcludeNulls() {
        // Given
        String username = "testUser";
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(100), new ArrayList<>(), username);
        AdReportEmailDto adReport1 = new AdReportEmailDto(
                AdType.TEXT, username, Instant.now(), 30, BigDecimal.valueOf(0), "test@example.com");
        AdReportEmailDto adReport2 = new AdReportEmailDto(
                AdType.TEXT_IMAGE, username, Instant.now(), 15, BigDecimal.valueOf(50), "test@example.com");

        when(paymentRepository.findGroupedPaymentsByPaymentTypeAndBetween(startInstant, endInstant))
                .thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentTypeAndBetween(startInstant, endInstant))
                .thenReturn(List.of(adReport1, adReport2));

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisers(startDate, endDate);

        // Then
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }

    /**
     * tests function getTotalPaymentsByAdvertisersById
     */
    @Test
    void givenValidUserId_whenGetTotalPaymentsByAdvertisersById_thenReturnUserReport() {
        // Given
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(150), new ArrayList<>(), username);
        AdReportEmailDto adReport = new AdReportEmailDto(
                AdType.TEXT, username, Instant.now(), 30, BigDecimal.valueOf(50), "test@example.com");

        when(paymentRepository.findGroupedPaymentsByPaymentTypeByIdUser(userId))
                .thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentTypeByIdUser(userId))
                .thenReturn(List.of(adReport));

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(1);
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.valueOf(50));

        PaymentPostAdPerAnnouncerDto enrichedPayment = result.paymentPostAdPerAnnouncerDtos().get(0);
        assertThat(enrichedPayment.username()).isEqualTo(username);
        assertThat(enrichedPayment.adReportDtos().size()).isEqualTo(1);

        verify(paymentRepository).findGroupedPaymentsByPaymentTypeByIdUser(userId);
        verify(paymentRepository).findAdReportsByPaymentTypeByIdUser(userId);
    }

    @Test
    void givenNonExistentUserId_whenGetTotalPaymentsByAdvertisersById_thenReturnEmptyReport() {
        // Given
        when(paymentRepository.findGroupedPaymentsByPaymentTypeByIdUser(userId))
                .thenReturn(Collections.emptyList());
        when(paymentRepository.findAdReportsByPaymentTypeByIdUser(userId))
                .thenReturn(Collections.emptyList());

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(0);
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenUserWithPaymentsButNoAds_whenGetTotalPaymentsByAdvertisersById_thenReturnReportWithZeroTotal() {
        // Given
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(200), new ArrayList<>(), username);

        when(paymentRepository.findGroupedPaymentsByPaymentTypeByIdUser(userId))
                .thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentTypeByIdUser(userId))
                .thenReturn(Collections.emptyList());

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(1);
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenUserWithAdsButNoPayments_whenGetTotalPaymentsByAdvertisersById_thenReturnEmptyPayments() {
        // Given
        AdReportEmailDto adReport = new AdReportEmailDto(
                AdType.TEXT, username, Instant.now(), 30, BigDecimal.valueOf(75), "test@example.com");

        when(paymentRepository.findGroupedPaymentsByPaymentTypeByIdUser(userId))
                .thenReturn(Collections.emptyList());
        when(paymentRepository.findAdReportsByPaymentTypeByIdUser(userId))
                .thenReturn(List.of(adReport));

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(0);
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.valueOf(75));
    }

    /**
     * tests function getTotalPaymentsByAdvertisersById
     */
    @Test
    void givenValidDatesAndUserId_whenGetTotalPaymentsByAdvertisersById_thenReturnFilteredReport() {
        // Given
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(200), new ArrayList<>(), username);
        AdReportEmailDto adReport = new AdReportEmailDto(
                AdType.TEXT, username, Instant.now(), 30, BigDecimal.valueOf(100), "test@example.com");

        when(paymentRepository.findGroupedPaymentsByPaymentTypeAndBetweenById(startInstant, endInstant, userId))
                .thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentTypeAndBetweenById(startInstant, endInstant, userId))
                .thenReturn(List.of(adReport));

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(startDate, endDate, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().size()).isEqualTo(1);
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.valueOf(100));

        verify(paymentRepository).findGroupedPaymentsByPaymentTypeAndBetweenById(startInstant, endInstant, userId);
        verify(paymentRepository).findAdReportsByPaymentTypeAndBetweenById(startInstant, endInstant, userId);
    }

    @Test
    void givenNullDates_whenGetTotalPaymentsByAdvertisersById_thenCallUnfilteredMethod() {
        // Given
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(150), new ArrayList<>(), username);

        when(paymentRepository.findGroupedPaymentsByPaymentTypeByIdUser(userId))
                .thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentTypeByIdUser(userId))
                .thenReturn(Collections.emptyList());

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(null, null, userId);

        // Then
        assertThat(result).isNotNull();
        verify(paymentRepository).findGroupedPaymentsByPaymentTypeByIdUser(userId);
        verify(paymentRepository).findAdReportsByPaymentTypeByIdUser(userId);
    }

    @Test
    void givenNullDates_whenGetTotalPaymentsByAdvertisersById2_thenCallUnfilteredMethod() {
        // Given
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(150), new ArrayList<>(), username);

        when(paymentRepository.findGroupedPaymentsByPaymentTypeByIdUser(userId))
                .thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentTypeByIdUser(userId))
                .thenReturn(Collections.emptyList());

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(startDate, null, userId);

        // Then
        assertThat(result).isNotNull();
        verify(paymentRepository).findGroupedPaymentsByPaymentTypeByIdUser(userId);
        verify(paymentRepository).findAdReportsByPaymentTypeByIdUser(userId);
    }

    @Test
    void givenNullDates_whenGetTotalPaymentsByAdvertisersById3_thenCallUnfilteredMethod() {
        // Given
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(150), new ArrayList<>(), username);

        when(paymentRepository.findGroupedPaymentsByPaymentTypeByIdUser(userId))
                .thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentTypeByIdUser(userId))
                .thenReturn(Collections.emptyList());

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(null, endDate, userId);

        // Then
        assertThat(result).isNotNull();
        verify(paymentRepository).findGroupedPaymentsByPaymentTypeByIdUser(userId);
        verify(paymentRepository).findAdReportsByPaymentTypeByIdUser(userId);
    }

    @Test
    void givenEmptyResults_whenGetTotalPaymentsByAdvertisersById_thenReturnEmptyReport() {
        // Given
        when(paymentRepository.findGroupedPaymentsByPaymentTypeAndBetweenById(startInstant, endInstant, userId))
                .thenReturn(Collections.emptyList());
        when(paymentRepository.findAdReportsByPaymentTypeAndBetweenById(startInstant, endInstant, userId))
                .thenReturn(Collections.emptyList());

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(startDate, endDate, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().isEmpty()).isTrue();
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenUserWithPaymentsButNoAds_whenGetTotalPaymentsByAdvertisersById2_thenReturnReportWithZeroTotal() {
        // Given
        PaymentPostAdPerAnnouncerDto payment = new PaymentPostAdPerAnnouncerDto(
                BigDecimal.valueOf(300), new ArrayList<>(), username);

        when(paymentRepository.findGroupedPaymentsByPaymentTypeAndBetweenById(startInstant, endInstant, userId))
                .thenReturn(List.of(payment));
        when(paymentRepository.findAdReportsByPaymentTypeAndBetweenById(startInstant, endInstant, userId))
                .thenReturn(Collections.emptyList());

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(startDate, endDate, userId);

        // Then
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void givenAdReportsWithMixedAmounts_whenGetTotalPaymentsByAdvertisersById_thenCalculateCorrectTotal() {
        // Given
        AdReportEmailDto ad1 = new AdReportEmailDto(
                AdType.TEXT, username, Instant.now(), 30, BigDecimal.valueOf(100), "test@example.com");
        AdReportEmailDto ad2 = new AdReportEmailDto(
                AdType.TEXT_IMAGE, username, Instant.now(), 15, BigDecimal.valueOf(50), "test@example.com");
        AdReportEmailDto ad3 = new AdReportEmailDto(
                AdType.VIDEO, username, Instant.now(), 7, BigDecimal.valueOf(0), "test@example.com");

        when(paymentRepository.findGroupedPaymentsByPaymentTypeAndBetweenById(startInstant, endInstant, userId))
                .thenReturn(Collections.emptyList());
        when(paymentRepository.findAdReportsByPaymentTypeAndBetweenById(startInstant, endInstant, userId))
                .thenReturn(List.of(ad1, ad2, ad3));

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(startDate, endDate, userId);

        // Then
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.valueOf(150));
    }

    @Test
    void givenStartDateAfterEndDate_whenGetTotalPaymentsByAdvertisersById_thenReturnEmptyReport() {
        // Given
        LocalDate invalidStartDate = endDate.plusDays(1);
        Instant invalidStartInstant = invalidStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        // When
        TotalReportPaymentPostAdByAnnouncersDto result =
                reportService.getTotalPaymentsByAdvertisersById(invalidStartDate, endDate, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.paymentPostAdPerAnnouncerDtos().isEmpty()).isTrue();
        assertThat(result.totalAdPost()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(paymentRepository).findGroupedPaymentsByPaymentTypeAndBetweenById(invalidStartInstant, endInstant, userId);
        verify(paymentRepository).findAdReportsByPaymentTypeAndBetweenById(invalidStartInstant, endInstant, userId);
    }

    /**
     * tests function getTopClear
     */
    @Test
    void givenSubscriptionsList_whenGetTopClear_thenReturnTop5Magazines() {
        // Given
        List<MagazineProjectionDto> subscriptions = List.of(
                createMagazineDto(1L, "Magazine 1", "editor1", 3),
                createMagazineDto(2L, "Magazine 2", "editor2", 5),
                createMagazineDto(3L, "Magazine 3", "editor3", 1),
                createMagazineDto(4L, "Magazine 4", "editor4", 4),
                createMagazineDto(5L, "Magazine 5", "editor5", 2),
                createMagazineDto(6L, "Magazine 6", "editor6", 6)
        );

        // When
        ReportTopMagazineSubscriptions result = reportService.getTopClear(subscriptions);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.subscriptions().size()).isEqualTo(5);

        // Verify order (should be sorted by subscription count descending)
        assertThat(result.subscriptions().get(0).title()).isEqualTo("Magazine 1");
        assertThat(result.subscriptions().get(0).subscriptionsDtos().size()).isEqualTo(1);
    }

    @Test
    void givenEmptySubscriptionsList_whenGetTopClear_thenReturnEmptyReport() {
        // Given
        List<MagazineProjectionDto> subscriptions = Collections.emptyList();

        // When
        ReportTopMagazineSubscriptions result = reportService.getTopClear(subscriptions);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.subscriptions().isEmpty()).isTrue();
    }

    @Test
    void givenLessThan5Magazines_whenGetTopClear_thenReturnAllMagazinesSorted() {
        // Given
        List<MagazineProjectionDto> subscriptions = List.of(
                createMagazineDto(1L, "Magazine 1", "editor1", 2),
                createMagazineDto(2L, "Magazine 2", "editor2", 3),
                createMagazineDto(3L, "Magazine 3", "editor3", 1)
        );

        // When
        ReportTopMagazineSubscriptions result = reportService.getTopClear(subscriptions);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.subscriptions().size()).isEqualTo(3);

        // Verify order
        assertThat(result.subscriptions().get(0).title()).isEqualTo("Magazine 1");
    }


    @Test
    void givenMagazinesWithSameCount_whenGetTopClear_thenMaintainStableOrder() {
        // Given
        List<MagazineProjectionDto> subscriptions = List.of(
                createMagazineDto(1L, "Magazine A", "editor1", 2),
                createMagazineDto(2L, "Magazine B", "editor2", 2),
                createMagazineDto(3L, "Magazine C", "editor3", 2)
        );

        // When
        ReportTopMagazineSubscriptions result = reportService.getTopClear(subscriptions);

        // Then
        assertThat(result.subscriptions().size()).isEqualTo(3);
    }


    /**
     * tests function getTop5MagazinesBySubscriptions
     */
    @Test
    void givenActiveSubscriptions_whenGetTop5MagazinesBySubscriptions_thenReturnTop5() {
        // Given
        List<MagazineProjectionDto> subscriptions = List.of(
                createMagazineDto(1L, "Magazine 1", "editor1", 3),
                createMagazineDto(2L, "Magazine 2", "editor2", 5),
                createMagazineDto(3L, "Magazine 3", "editor3", 1),
                createMagazineDto(4L, "Magazine 4", "editor4", 4),
                createMagazineDto(5L, "Magazine 5", "editor5", 2),
                createMagazineDto(6L, "Magazine 6", "editor6", 6)
        );

        when(subscriptionRepository.findAllActiveSubscriptionDtos()).thenReturn(subscriptions);

        // When
        ReportTopMagazineSubscriptions result = reportService.getTop5MagazinesBySubscriptions();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.subscriptions().size()).isEqualTo(5);

        // Verify order by subscription count descending
        assertThat(result.subscriptions().get(0).title()).isEqualTo("Magazine 1");

    }

    @Test
    void givenEmptySubscriptions_whenGetTop5MagazinesBySubscriptions_thenReturnEmptyReport() {
        // Given
        when(subscriptionRepository.findAllActiveSubscriptionDtos()).thenReturn(Collections.emptyList());

        // When
        ReportTopMagazineSubscriptions result = reportService.getTop5MagazinesBySubscriptions();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.subscriptions().isEmpty()).isTrue();
    }

    @Test
    void givenLessThan5Magazines_whenGetTop5MagazinesBySubscriptions_thenReturnAll() {
        // Given
        List<MagazineProjectionDto> subscriptions = List.of(
                createMagazineDto(1L, "Magazine 1", "editor1", 2),
                createMagazineDto(2L, "Magazine 2", "editor2", 3)
        );

        when(subscriptionRepository.findAllActiveSubscriptionDtos()).thenReturn(subscriptions);

        // When
        ReportTopMagazineSubscriptions result = reportService.getTop5MagazinesBySubscriptions();

        // Then
        assertThat(result.subscriptions().size()).isEqualTo(2);
    }


    @Test
    void givenMagazinesWithSameCount_whenGetTop5MagazinesBySubscriptions_thenMaintainOrder() {
        // Given
        List<MagazineProjectionDto> subscriptions = List.of(
                createMagazineDto(1L, "Magazine A", "editor1", 2),
                createMagazineDto(2L, "Magazine B", "editor2", 2),
                createMagazineDto(3L, "Magazine C", "editor3", 2)
        );

        when(subscriptionRepository.findAllActiveSubscriptionDtos()).thenReturn(subscriptions);

        // When
        ReportTopMagazineSubscriptions result = reportService.getTop5MagazinesBySubscriptions();

        // Then
        assertThat(result.subscriptions().size()).isEqualTo(3);

    }

    /**
     * tests function getTop5MagazinesBySubscriptionsRange
     */
    @Test
    void givenValidDateRange_whenGetTop5MagazinesBySubscriptionsRange_thenReturnFilteredTopMagazines() {
        // Given
        MagazineProjectionDto magazine1 = createMagazineDto(1L, "Magazine 1", "editor1", 3);
        MagazineProjectionDto magazine2 = createMagazineDto(2L, "Magazine 2", "editor2", 5);

        when(subscriptionRepository.findAllActiveSubscriptionDtosBetween(startInstant, endInstant))
                .thenReturn(List.of(magazine1, magazine2));

        // When
        ReportTopMagazineSubscriptions result = reportService.getTop5MagazinesBySubscriptionsRange(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.subscriptions().size()).isEqualTo(2);
        assertThat(result.subscriptions().get(0).title()).isEqualTo("Magazine 1");
        assertThat(result.subscriptions().get(0).subscriptionsDtos().size()).isEqualTo(1);
        verify(subscriptionRepository).findAllActiveSubscriptionDtosBetween(startInstant, endInstant);
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    void givenNullStartDate_whenGetTop5MagazinesBySubscriptionsRange_thenCallUnfilteredMethod() {
        // Given
        MagazineProjectionDto magazine = createMagazineDto(1L, "Magazine 1", "editor1", 2);

        when(subscriptionRepository.findAllActiveSubscriptionDtos())
                .thenReturn(List.of(magazine));

        // When
        ReportTopMagazineSubscriptions result = reportService.getTop5MagazinesBySubscriptionsRange(null, endDate);

        // Then
        assertThat(result).isNotNull();
        verify(subscriptionRepository).findAllActiveSubscriptionDtos();
    }

    @Test
    void givenNullEndDate_whenGetTop5MagazinesBySubscriptionsRange_thenCallUnfilteredMethod() {
        // Given
        MagazineProjectionDto magazine = createMagazineDto(1L, "Magazine 1", "editor1", 2);

        when(subscriptionRepository.findAllActiveSubscriptionDtos())
                .thenReturn(List.of(magazine));

        // When
        ReportTopMagazineSubscriptions result = reportService.getTop5MagazinesBySubscriptionsRange(startDate, null);

        // Then
        assertThat(result).isNotNull();
        verify(subscriptionRepository).findAllActiveSubscriptionDtos();
    }

    @Test
    void givenEmptyResults_whenGetTop5MagazinesBySubscriptionsRange_thenReturnEmptyReport() {
        // Given
        when(subscriptionRepository.findAllActiveSubscriptionDtosBetween(startInstant, endInstant))
                .thenReturn(Collections.emptyList());

        // When
        ReportTopMagazineSubscriptions result = reportService.getTop5MagazinesBySubscriptionsRange(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.subscriptions().isEmpty()).isTrue();
    }

    @Test
    void givenStartDateAfterEndDate_whenGetTop5MagazinesBySubscriptionsRange_thenReturnEmptyReport() {
        // Given
        LocalDate invalidStartDate = endDate.plusDays(1);
        Instant invalidStartInstant = invalidStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(subscriptionRepository.findAllActiveSubscriptionDtosBetween(invalidStartInstant, endInstant))
                .thenReturn(Collections.emptyList());

        // When
        ReportTopMagazineSubscriptions result = reportService.getTop5MagazinesBySubscriptionsRange(invalidStartDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.subscriptions().isEmpty());
    }

    private MagazineProjectionDto createMagazineDto(Long id, String title, String editor, int subscriptionCount) {
        List<MagazineProjectionDto> subscriptions = IntStream.range(0, subscriptionCount)
                .mapToObj(i -> new MagazineProjectionDto(
                        id,
                        "user" + i,
                        editor,
                        "user" + i + "@example.com",
                        title,
                        Instant.now(),
                        Instant.now()
                ))
                .collect(Collectors.toList());

        return subscriptions.get(0);
    }

    /**
     * tests function getTopClearComments
     */
    @Test
    void givenCommentsList_whenGetTopClearComments_thenReturnTop5Magazines() {
        // Given
        List<MagazineProjectionCommentsDto> comments = List.of(
                createMagazineCommentDto(1L, "Magazine 1", "editor1", 3),
                createMagazineCommentDto(2L, "Magazine 2", "editor2", 5),
                createMagazineCommentDto(3L, "Magazine 3", "editor3", 1),
                createMagazineCommentDto(4L, "Magazine 4", "editor4", 4),
                createMagazineCommentDto(5L, "Magazine 5", "editor5", 2),
                createMagazineCommentDto(6L, "Magazine 6", "editor6", 6) // Should be first
        );

        // When
        ReportMagazineCommentsDto result = reportService.getTopClearComments(comments);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(5);

        // Verify
        assertThat(result.magazineCommentsDtoList().get(0).title()).isEqualTo("Magazine 1");
        assertThat(result.magazineCommentsDtoList().get(0).comments().size()).isEqualTo(1);

    }

    @Test
    void givenEmptyCommentsList_whenGetTopClearComments_thenReturnEmptyReport() {
        // Given
        List<MagazineProjectionCommentsDto> comments = Collections.emptyList();

        // When
        ReportMagazineCommentsDto result = reportService.getTopClearComments(comments);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.magazineCommentsDtoList().isEmpty()).isTrue();
    }

    @Test
    void givenLessThan5Magazines_whenGetTopClearComments_thenReturnAll() {
        // Given
        List<MagazineProjectionCommentsDto> comments = List.of(
                createMagazineCommentDto(1L, "Magazine 1", "editor1", 2),
                createMagazineCommentDto(2L, "Magazine 2", "editor2", 3)
        );

        // When
        ReportMagazineCommentsDto result = reportService.getTopClearComments(comments);

        // Then
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(2);
    }


    @Test
    void givenMagazinesWithSameCount_whenGetTopClearComments_thenMaintainStableOrder() {
        // Given
        List<MagazineProjectionCommentsDto> comments = List.of(
                createMagazineCommentDto(1L, "Magazine A", "editor1", 2),
                createMagazineCommentDto(2L, "Magazine B", "editor2", 2),
                createMagazineCommentDto(3L, "Magazine C", "editor3", 2)
        );

        // When
        ReportMagazineCommentsDto result = reportService.getTopClearComments(comments);

        // Then
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(3);

    }


    @Test
    void givenCommentWithNullFields_whenGetTopClearComments_thenHandleGracefully() {
        // Given
        MagazineProjectionCommentsDto comment = new MagazineProjectionCommentsDto(
                1L, null, null, null, null, null, null, null);

        // When
        ReportMagazineCommentsDto result = reportService.getTopClearComments(List.of(comment));

        // Then
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(1);
        MagazineCommentsDto magazine = result.magazineCommentsDtoList().get(0);
        assertThat(magazine.title()).isNull();
        assertThat(magazine.usernameEditor()).isNull();
        assertThat(magazine.comments().size()).isEqualTo(1);
    }

    /**
     * tests function getTop5MagazinesByComments
     */
    @Test
    void givenCommentsExist_whenGetTop5MagazinesByComments_thenReturnTop5() {
        // Given
        List<MagazineProjectionCommentsDto> comments = List.of(
                createMagazineCommentDto(1L, "Magazine 1", "editor1", 3),
                createMagazineCommentDto(2L, "Magazine 2", "editor2", 5),
                createMagazineCommentDto(3L, "Magazine 3", "editor3", 1),
                createMagazineCommentDto(4L, "Magazine 4", "editor4", 4),
                createMagazineCommentDto(5L, "Magazine 5", "editor5", 2),
                createMagazineCommentDto(6L, "Magazine 6", "editor6", 6) // Should be first
        );

        when(commentRepository.findAllCommentsDtos()).thenReturn(comments);

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByComments();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(5);

        // Verify order by comment count descending
        assertThat(result.magazineCommentsDtoList().get(0).title()).isEqualTo("Magazine 1");
        assertThat(result.magazineCommentsDtoList().get(0).comments().size()).isEqualTo(1);
        verify(commentRepository).findAllCommentsDtos();

    }

    @Test
    void givenNoComments_whenGetTop5MagazinesByComments_thenReturnEmptyReport() {
        // Given
        when(commentRepository.findAllCommentsDtos()).thenReturn(Collections.emptyList());

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByComments();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.magazineCommentsDtoList().isEmpty()).isTrue();
    }

    @Test
    void givenLessThan5Magazines_whenGetTop5MagazinesByComments_thenReturnAvailable() {
        // Given
        List<MagazineProjectionCommentsDto> comments = List.of(
                createMagazineCommentDto(1L, "Magazine 1", "editor1", 2),
                createMagazineCommentDto(2L, "Magazine 2", "editor2", 3)
        );

        when(commentRepository.findAllCommentsDtos()).thenReturn(comments);

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByComments();

        // Then
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(2);
    }


    @Test
    void givenMagazinesWithSameCount_whenGetTop5MagazinesByComments_thenMaintainOrder() {
        // Given
        List<MagazineProjectionCommentsDto> comments = List.of(
                createMagazineCommentDto(1L, "Magazine A", "editor1", 2),
                createMagazineCommentDto(2L, "Magazine B", "editor2", 2),
                createMagazineCommentDto(3L, "Magazine C", "editor3", 2)
        );

        when(commentRepository.findAllCommentsDtos()).thenReturn(comments);

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByComments();

        // Then
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(3);
    }

    @Test
    void givenCommentWithNullFields_whenGetTop5MagazinesByComments_thenHandleGracefully() {
        // Given
        MagazineProjectionCommentsDto comment = new MagazineProjectionCommentsDto(
                1L, null, null, null, null, null, null, null);

        when(commentRepository.findAllCommentsDtos()).thenReturn(List.of(comment));

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByComments();

        // Then
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(1);
        MagazineCommentsDto magazine = result.magazineCommentsDtoList().get(0);
        assertThat(magazine.title()).isNull();
        assertThat(magazine.usernameEditor()).isNull();
        assertThat(magazine.comments().size()).isEqualTo(1);
    }

    private MagazineProjectionCommentsDto createMagazineCommentDto(Long id, String title, String editor, int commentCount) {
        List<MagazineProjectionCommentsDto> comments = IntStream.range(0, commentCount)
                .mapToObj(i -> new MagazineProjectionCommentsDto(
                        id,
                        "user" + i,
                        editor,
                        "user" + i + "@example.com",
                        title,
                        "Comment content " + i,
                        Instant.now(),
                        Instant.now()
                ))
                .collect(Collectors.toList());

        return comments.get(0);
    }

    /**
     * tests function getTop5MagazinesByCommentsRange
     */
    @Test
    void givenValidDateRange_whenGetTop5MagazinesByCommentsRange_thenReturnFilteredTopMagazines() {
        // Given
        MagazineProjectionCommentsDto magazine1 = createMagazineCommentDto(1L, "Magazine 1", "editor1", 3);
        MagazineProjectionCommentsDto magazine2 = createMagazineCommentDto(2L, "Magazine 2", "editor2", 5);

        when(commentRepository.findAllCommentsDtosBetween(startInstant, endInstant))
                .thenReturn(List.of(magazine1, magazine2));

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByCommentsRange(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(2);
        assertThat(result.magazineCommentsDtoList().get(0).title()).isEqualTo("Magazine 1");
        assertThat(result.magazineCommentsDtoList().get(0).comments().size()).isEqualTo(1);

        verify(commentRepository).findAllCommentsDtosBetween(startInstant, endInstant);
    }

    @Test
    void givenNullStartDate_whenGetTop5MagazinesByCommentsRange_thenCallUnfilteredMethod() {
        // Given
        MagazineProjectionCommentsDto magazine = createMagazineCommentDto(1L, "Magazine 1", "editor1", 2);

        when(commentRepository.findAllCommentsDtos())
                .thenReturn(List.of(magazine));

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByCommentsRange(null, endDate);

        // Then
        assertThat(result).isNotNull();
        verify(commentRepository).findAllCommentsDtos();
    }

    @Test
    void givenNullEndDate_whenGetTop5MagazinesByCommentsRange_thenCallUnfilteredMethod() {
        // Given
        MagazineProjectionCommentsDto magazine = createMagazineCommentDto(1L, "Magazine 1", "editor1", 2);

        when(commentRepository.findAllCommentsDtos())
                .thenReturn(List.of(magazine));

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByCommentsRange(startDate, null);

        // Then
        assertThat(result).isNotNull();
        verify(commentRepository).findAllCommentsDtos();
    }

    @Test
    void givenEmptyResults_whenGetTop5MagazinesByCommentsRange_thenReturnEmptyReport() {
        // Given
        when(commentRepository.findAllCommentsDtosBetween(startInstant, endInstant))
                .thenReturn(Collections.emptyList());

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByCommentsRange(startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.magazineCommentsDtoList().isEmpty()).isTrue();
    }

    @Test
    void givenStartDateAfterEndDate_whenGetTop5MagazinesByCommentsRange_thenReturnEmptyReport() {
        // Given
        LocalDate invalidStartDate = endDate.plusDays(1);
        Instant invalidStartInstant = invalidStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(commentRepository.findAllCommentsDtosBetween(invalidStartInstant, endInstant))
                .thenReturn(Collections.emptyList());

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByCommentsRange(invalidStartDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.magazineCommentsDtoList().isEmpty()).isTrue();
    }

    @Test
    void givenMagazinesWithSameCount_whenGetTop5MagazinesByCommentsRange_thenMaintainOrder() {
        // Given
        List<MagazineProjectionCommentsDto> comments = List.of(
                createMagazineCommentDto(1L, "Magazine A", "editor1", 2),
                createMagazineCommentDto(2L, "Magazine B", "editor2", 2)
        );

        when(commentRepository.findAllCommentsDtosBetween(startInstant, endInstant))
                .thenReturn(comments);

        // When
        ReportMagazineCommentsDto result = reportService.getTop5MagazinesByCommentsRange(startDate, endDate);

        // Then
        assertThat(result.magazineCommentsDtoList().size()).isEqualTo(2);
    }

}