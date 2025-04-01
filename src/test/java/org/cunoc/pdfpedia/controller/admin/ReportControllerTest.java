package org.cunoc.pdfpedia.controller.admin;

import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.EarningsReport;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineCostTotalDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.AdReportEmailDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.PaymentPostAdPerAnnouncerDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.TotalReportPaymentPostAdByAnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.postAd.PostAdReportTotal;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.CommentDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.MagazineCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.ReportMagazineCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.MagazineSubscriptions;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.ReportTopMagazineSubscriptions;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.SubscriptionsDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.CountRegisterByRolDto;
import org.cunoc.pdfpedia.domain.type.AdType;
import org.cunoc.pdfpedia.service.admin.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @InjectMocks
    private ReportController reportController;

    @Mock
    private ReportService reportService;

    private CountRegisterByRolDto countRegisterByRolDto;
    private List<CountRegisterByRolDto> countRegisterByRolDtoList;
    private EarningsReport earningsReport;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer type;
    private PostAdReportTotal postAdReportTotal;
    private TotalReportPaymentPostAdByAnnouncersDto totalReportPaymentPostAdByAnnouncersDto;
    private ReportTopMagazineSubscriptions reportTopMagazineSubscriptions;
    private ReportMagazineCommentsDto reportMagazineCommentsDto;

    @BeforeEach
    void setUp() {
        countRegisterByRolDto = new CountRegisterByRolDto("Admin", 5L);
        countRegisterByRolDtoList = List.of(countRegisterByRolDto);
        earningsReport = EarningsReport.builder()
                .totalEarnings(BigDecimal.valueOf(1000))
                .totalAdPost(BigDecimal.valueOf(500))
                .totalAdBlocks(BigDecimal.valueOf(200))
                .totalCostPerDay(BigDecimal.valueOf(50))
                .totalIncome(BigDecimal.valueOf(1000))
                .adReportDto(List.of(
                        AdReportDto.builder()
                                .adType(AdType.TEXT)
                                .username("user1")
                                .datePay(Instant.now())
                                .plan(1)
                                .amount(BigDecimal.valueOf(100))
                                .build()
                ))
                .magazineReportDto(List.of(
                        MagazineReportDto.builder()
                                .title("Magazine 1")
                                .editor("Editor1")
                                .datePay(Instant.now())
                                .amount(BigDecimal.valueOf(300))
                                .build()
                ))
                .magazineCostTotalDto(List.of(
                        MagazineCostTotalDto.builder()
                                .title("Magazine 1")
                                .costPerDay(BigDecimal.valueOf(30))
                                .createdAt(Instant.now())
                                .username("user1")
                                .costTotal(BigDecimal.valueOf(200))
                                .build()
                ))
                .range("Last Week")
                .build();

        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);
        type = 1;
        postAdReportTotal = PostAdReportTotal.builder()
                .adReportDto(List.of(
                        new AdReportDto(AdType.TEXT_IMAGE, "user1", Instant.now(), 1, BigDecimal.TEN)
                ))
                .totalAdPost(BigDecimal.TEN)
                .range("2024")
                .filter("type=1")
                .build();

        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);

        totalReportPaymentPostAdByAnnouncersDto = TotalReportPaymentPostAdByAnnouncersDto.builder()
                .paymentPostAdPerAnnouncerDtos(List.of(
                        PaymentPostAdPerAnnouncerDto.builder()
                                .username("user1")
                                .amountTotal(BigDecimal.valueOf(100))
                                .adReportDtos(List.of(
                                        AdReportEmailDto.builder()
                                                .adType(AdType.TEXT)
                                                .username("user1")
                                                .datePay(Instant.now())
                                                .plan(1)
                                                .amount(BigDecimal.valueOf(100))
                                                .email("user1@example.com")
                                                .build()
                                ))
                                .build()
                ))
                .totalAdPost(BigDecimal.valueOf(500))
                .range("2024")
                .filter("type=1")
                .build();

        reportTopMagazineSubscriptions = ReportTopMagazineSubscriptions.builder()
                .range("2024")
                .subscriptions(List.of(
                        MagazineSubscriptions.builder()
                                .title("Magazine 1")
                                .usernameEditor("Editor 1")
                                .createdAt(Instant.now())
                                .subscriptionsDtos(List.of(
                                        SubscriptionsDto.builder()
                                                .usernameSubscriber("Subscriber 1")
                                                .emailSubscriber("subscriber1@example.com")
                                                .subscribedAt(Instant.now())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        reportMagazineCommentsDto = ReportMagazineCommentsDto.builder()
                .magazineCommentsDtoList(List.of(
                        MagazineCommentsDto.builder()
                                .title("Magazine 1")
                                .usernameEditor("Editor1")
                                .createdAt(Instant.now())
                                .comments(List.of(
                                        CommentDto.builder()
                                                .usernameComment("User1")
                                                .emailComment("user1@example.com")
                                                .contentComment("Great Magazine!")
                                                .commentAt(Instant.now())
                                                .build()
                                ))
                                .build()
                ))
                .range("2024")
                .build();

    }

    @Test
    void givenStartDateAndEndDateWhenGetCountByRoleThenReturnList() {
        // given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(reportService.findCountRegisterByRol(startDate, endDate)).thenReturn(countRegisterByRolDtoList);

        // when
        ResponseEntity<List<CountRegisterByRolDto>> response = reportController.getCountByRole(startDate, endDate);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(countRegisterByRolDtoList);
        verify(reportService).findCountRegisterByRol(startDate, endDate);
    }

    @Test
    void givenNoDatesWhenGetCountByRoleThenReturnList() {
        // given
        when(reportService.findCountRegisterByRol(null, null)).thenReturn(countRegisterByRolDtoList);

        // when
        ResponseEntity<List<CountRegisterByRolDto>> response = reportController.getCountByRole(null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(countRegisterByRolDtoList);
        verify(reportService).findCountRegisterByRol(null, null);
    }

    @Test
    void givenEmptyResultWhenGetCountByRoleThenReturnEmptyList() {
        // given
        List<CountRegisterByRolDto> emptyList = List.of();
        when(reportService.findCountRegisterByRol(null, null)).thenReturn(emptyList);

        // when
        ResponseEntity<List<CountRegisterByRolDto>> response = reportController.getCountByRole(null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(emptyList);
        verify(reportService).findCountRegisterByRol(null, null);
    }

    /**
     * test function getTotalReportEarnings
     */
    @Test
    void givenStartDateAndEndDateWhenGetTotalReportEarningsThenReturnEarningsReport() {
        // given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(reportService.getTotalReportEarnings(startDate, endDate)).thenReturn(earningsReport);

        // when
        ResponseEntity<EarningsReport> response = reportController.getTotalReportEarnings(startDate, endDate);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(earningsReport);
        verify(reportService).getTotalReportEarnings(startDate, endDate);
    }

    @Test
    void givenNoDatesWhenGetTotalReportEarningsThenReturnEarningsReport() {
        // given
        when(reportService.getTotalReportEarnings(null, null)).thenReturn(earningsReport);

        // when
        ResponseEntity<EarningsReport> response = reportController.getTotalReportEarnings(null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(earningsReport);
        verify(reportService).getTotalReportEarnings(null, null);
    }

    @Test
    void givenNullResultWhenGetTotalReportEarningsThenReturnEmpty() {
        // given
        when(reportService.getTotalReportEarnings(null, null)).thenReturn(null);

        // when
        ResponseEntity<EarningsReport> response = reportController.getTotalReportEarnings(null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
        verify(reportService).getTotalReportEarnings(null, null);
    }

    /**
     * test function getTotalReportEarnings
     */
    @Test
    void givenValidParams_whenGetTotalReportEarnings_thenReturnsPostAdReportTotal() {
        // Given
        when(reportService.getPostAdReportTotal(startDate, endDate, type)).thenReturn(postAdReportTotal);

        // When
        ResponseEntity<PostAdReportTotal> response = reportController.getTotalReportEarnings(startDate, endDate, type);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(postAdReportTotal, response.getBody());
    }

    @Test
    void givenNullParams_whenGetTotalReportEarnings_thenReturnsPostAdReportTotal() {
        // Given
        when(reportService.getPostAdReportTotal(null, null, type)).thenReturn(postAdReportTotal);

        // When
        ResponseEntity<PostAdReportTotal> response = reportController.getTotalReportEarnings(null, null, type);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(postAdReportTotal, response.getBody());
    }

    @Test
    void givenEmptyReport_whenGetTotalReportEarnings_thenReturnsEmptyReport() {
        // Given
        PostAdReportTotal emptyReport = PostAdReportTotal.builder()
                .adReportDto(Collections.emptyList())
                .totalAdPost(BigDecimal.ZERO)
                .range("")
                .filter("")
                .build();
        when(reportService.getPostAdReportTotal(startDate, endDate, type)).thenReturn(emptyReport);

        // When
        ResponseEntity<PostAdReportTotal> response = reportController.getTotalReportEarnings(startDate, endDate, type);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(emptyReport, response.getBody());
    }

    @Test
    void givenInvalidParams_whenGetTotalReportEarnings_thenThrowsException() {
        // Given
        when(reportService.getPostAdReportTotal(startDate, endDate, type)).thenThrow(new RuntimeException("Error in service"));

        // When & Then
        assertThrows(RuntimeException.class, () -> reportController.getTotalReportEarnings(startDate, endDate, type));
    }

    /**
     * tests function getTotalReportEarningsPostAdByAnnouncers
     */
    @Test
    void givenValidParams_whenGetTotalReportEarningsPostAdByAnnouncers_thenReturnsTotalReportPaymentPostAdByAnnouncersDto() {
        // Given
        when(reportService.getTotalPaymentsByAdvertisers(startDate, endDate)).thenReturn(totalReportPaymentPostAdByAnnouncersDto);

        // When
        ResponseEntity<TotalReportPaymentPostAdByAnnouncersDto> response = reportController.getTotalReportEarningsPostAdByAnnouncers(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(totalReportPaymentPostAdByAnnouncersDto, response.getBody());
    }

    @Test
    void givenNullParams_whenGetTotalReportEarningsPostAdByAnnouncers_thenReturnsTotalReportPaymentPostAdByAnnouncersDto() {
        // Given
        when(reportService.getTotalPaymentsByAdvertisers(null, null)).thenReturn(totalReportPaymentPostAdByAnnouncersDto);

        // When
        ResponseEntity<TotalReportPaymentPostAdByAnnouncersDto> response = reportController.getTotalReportEarningsPostAdByAnnouncers(null, null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(totalReportPaymentPostAdByAnnouncersDto, response.getBody());
    }

    @Test
    void givenEmptyReport_whenGetTotalReportEarningsPostAdByAnnouncers_thenReturnsEmptyReport() {
        // Given
        TotalReportPaymentPostAdByAnnouncersDto emptyReport = TotalReportPaymentPostAdByAnnouncersDto.builder()
                .paymentPostAdPerAnnouncerDtos(Collections.emptyList())
                .totalAdPost(BigDecimal.ZERO)
                .range("")
                .filter("")
                .build();
        when(reportService.getTotalPaymentsByAdvertisers(startDate, endDate)).thenReturn(emptyReport);

        // When
        ResponseEntity<TotalReportPaymentPostAdByAnnouncersDto> response = reportController.getTotalReportEarningsPostAdByAnnouncers(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(emptyReport, response.getBody());
    }

    @Test
    void givenInvalidParams_whenGetTotalReportEarningsPostAdByAnnouncers_thenThrowsException() {
        // Given
        when(reportService.getTotalPaymentsByAdvertisers(startDate, endDate)).thenThrow(new RuntimeException("Error in service"));

        // When & Then
        assertThrows(RuntimeException.class, () -> reportController.getTotalReportEarningsPostAdByAnnouncers(startDate, endDate));
    }

    /**
     * tests function getTotalReportEarningsPostAdByAnnouncersById
     */
    @Test
    void givenValidParameters_whenGetTotalReportEarningsPostAdByAnnouncersById_thenReturnReport() {
        // Arrange
        Long id = 1L;
        // Mock the service call
        when(reportService.getTotalPaymentsByAdvertisersById(startDate, endDate, id))
                .thenReturn(totalReportPaymentPostAdByAnnouncersDto);

        // Act
        ResponseEntity<TotalReportPaymentPostAdByAnnouncersDto> response = reportController.getTotalReportEarningsPostAdByAnnouncersById(startDate, endDate, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user1", response.getBody().paymentPostAdPerAnnouncerDtos().get(0).username());
        assertEquals(BigDecimal.valueOf(100), response.getBody().paymentPostAdPerAnnouncerDtos().get(0).amountTotal());
    }

    @Test
    void givenNullParameters_whenGetTotalReportEarningsPostAdByAnnouncersById_thenReturnReport() {
        // Arrange
        Long id = 1L;
        startDate = null;
        endDate = null;
        // Mock the service call
       when(reportService.getTotalPaymentsByAdvertisersById(startDate, endDate, id))
                .thenReturn(totalReportPaymentPostAdByAnnouncersDto);

        // Act
        ResponseEntity<TotalReportPaymentPostAdByAnnouncersDto> response = reportController.getTotalReportEarningsPostAdByAnnouncersById(startDate, endDate, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /**
     * tests function getTop5MagazinesBySubscriptions
     */
    @Test
    void givenNoDateRange_whenGetTop5MagazinesBySubscriptions_thenReturnTopMagazines() {
        // Arrange
        when(reportService.getTop5MagazinesBySubscriptionsRange(null, null))
                .thenReturn(reportTopMagazineSubscriptions);

        // Act
        ResponseEntity<ReportTopMagazineSubscriptions> response = reportController.getTop5MagazinesBySubscriptions(null, null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(reportTopMagazineSubscriptions, response.getBody());
    }

    @Test
    void givenDateRange_whenGetTop5MagazinesBySubscriptions_thenReturnTopMagazines() {
        // Arrange
        when(reportService.getTop5MagazinesBySubscriptionsRange(startDate, endDate))
                .thenReturn(reportTopMagazineSubscriptions);

        // Act
        ResponseEntity<ReportTopMagazineSubscriptions> response = reportController.getTop5MagazinesBySubscriptions(startDate, endDate);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(reportTopMagazineSubscriptions, response.getBody());
    }

    /**
     * tests function getTop5MagazinesByComments
     */
    @Test
    void givenValidDateRange_whenGetTop5MagazinesByComments_thenReturnMagazineComments() {
        // Given
        when(reportService.getTop5MagazinesByCommentsRange(startDate, endDate))
                .thenReturn(reportMagazineCommentsDto);

        // When
        ResponseEntity<ReportMagazineCommentsDto> response = reportController.getTop5MagazinesByComments(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("2024", response.getBody().range());
        assertFalse(response.getBody().magazineCommentsDtoList().isEmpty());
        assertEquals("Magazine 1", response.getBody().magazineCommentsDtoList().get(0).title());
    }

    @Test
    void givenValidDateRange_whenGetTop5MagazinesByComments_andServiceReturnsEmpty_thenReturnEmptyList() {
        // Given
        when(reportService.getTop5MagazinesByCommentsRange(startDate, endDate))
                .thenReturn(ReportMagazineCommentsDto.builder()
                        .magazineCommentsDtoList(List.of())
                        .range("2024")
                        .build());

        // When
        ResponseEntity<ReportMagazineCommentsDto> response = reportController.getTop5MagazinesByComments(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().magazineCommentsDtoList().isEmpty());
    }

    @Test
    void givenNullDateRange_whenGetTop5MagazinesByComments_thenReturnMagazineComments() {
        // Given
        when(reportService.getTop5MagazinesByCommentsRange(null, null))
                .thenReturn(reportMagazineCommentsDto);

        // When
        ResponseEntity<ReportMagazineCommentsDto> response = reportController.getTop5MagazinesByComments(null, null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("2024", response.getBody().range());
        assertFalse(response.getBody().magazineCommentsDtoList().isEmpty());
        assertEquals("Magazine 1", response.getBody().magazineCommentsDtoList().get(0).title());
    }

    @Test
    void givenInvalidDateRange_whenGetTop5MagazinesByComments_thenReturnEmptyList() {
        // Given
        when(reportService.getTop5MagazinesByCommentsRange(startDate, endDate))
                .thenReturn(ReportMagazineCommentsDto.builder()
                        .magazineCommentsDtoList(List.of())
                        .range("2024")
                        .build());

        // When
        ResponseEntity<ReportMagazineCommentsDto> response = reportController.getTop5MagazinesByComments(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().magazineCommentsDtoList().isEmpty());
    }


}