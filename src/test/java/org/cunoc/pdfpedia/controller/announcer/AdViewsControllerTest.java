package org.cunoc.pdfpedia.controller.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.service.announcer.AdViewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdViewsControllerTest {

    @InjectMocks
    private AdViewsController adViewsController;

    @Mock
    private AdViewsService adViewsService;

    private AdViewCreateDto adViewCreateDto;
    private TotalViewsAdDto totalViewsAdDto;
    private long userId;
    private List<PostAdMount> postAdMounts;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<AdViewReportDto> adViewReportDtos;


    @BeforeEach
    void setUp() {
        userId = 1L;
        adViewCreateDto = new AdViewCreateDto(1L, "https://example.com/ad-view");
        totalViewsAdDto = new TotalViewsAdDto(100);
        postAdMounts = List.of(
                new PostAdMount("January", 10),
                new PostAdMount("February", 5)
        );
        startDate = LocalDate.now().minusDays(7);
        endDate = LocalDate.now();

        AdDto adDto = new AdDto(1L, 1L, "Content", "https://example.com/image.jpg", "https://example.com/video.mp4", Instant.now(), LocalDateTime.now().plusDays(30), true, null);
        ViewAdDto viewAdDto = new ViewAdDto("https://example.com/ad-view", Instant.now());
        adViewReportDtos = List.of(new AdViewReportDto(adDto, List.of(viewAdDto)));
    }

    @Test
    void givenAdViewCreateDto_whenAddAdViews_thenReturnCreatedStatus() {
        // Given

        // When
        adViewsController.addAdViews(adViewCreateDto);

        // Then
        verify(adViewsService).create(adViewCreateDto);
    }

    @Test
    void givenUserId_whenGetTotal_thenReturnTotalViewsAdDto() {
        // Given
        when(adViewsService.getTotalViews(userId)).thenReturn(totalViewsAdDto);

        // When
        ResponseEntity<TotalViewsAdDto> response = adViewsController.getTotal(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(totalViewsAdDto, response.getBody());
    }

    @Test
    void givenUserId_whenGetTotal_thenReturnZeroTotalViewsAdDto() {
        // Given
        TotalViewsAdDto zeroTotalViewsAdDto = new TotalViewsAdDto(0);
        when(adViewsService.getTotalViews(userId)).thenReturn(zeroTotalViewsAdDto);

        // When
        ResponseEntity<TotalViewsAdDto> response = adViewsController.getTotal(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(zeroTotalViewsAdDto, response.getBody());
    }

    @Test
    void givenUserId_whenGetAllPostAdMount_thenReturnListOfPostAdMount() {
        // Given
        when(adViewsService.getPostMount(userId)).thenReturn(postAdMounts);

        // When
        ResponseEntity<List<PostAdMount>> response = adViewsController.getAllPostAdMount(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postAdMounts, response.getBody());
    }

    @Test
    void givenUserId_whenGetAllPostAdMount_thenReturnEmptyList() {
        // Given
        when(adViewsService.getPostMount(userId)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<PostAdMount>> response = adViewsController.getAllPostAdMount(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }


    @Test
    void givenStartDateEndDateAndUserId_whenGetReportViews_thenReturnListOfAdViewReportDto() {
        // Given
        when(adViewsService.getReportViews(startDate, endDate, userId)).thenReturn(adViewReportDtos);

        // When
        ResponseEntity<List<AdViewReportDto>> response = adViewsController.getReportViews(startDate, endDate, userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adViewReportDtos, response.getBody());
    }

    @Test
    void givenNullDatesAndUserId_whenGetReportViews_thenReturnListOfAdViewReportDto() {
        // Given
        when(adViewsService.getReportViews(null, null, userId)).thenReturn(adViewReportDtos);

        // When
        ResponseEntity<List<AdViewReportDto>> response = adViewsController.getReportViews(null, null, userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adViewReportDtos, response.getBody());
    }

    @Test
    void givenStartDateEndDateAndUserId_whenGetReportViews_thenReturnEmptyList() {
        // Given
        when(adViewsService.getReportViews(startDate, endDate, userId)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<AdViewReportDto>> response = adViewsController.getReportViews(startDate, endDate, userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

}