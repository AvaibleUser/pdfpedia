package org.cunoc.pdfpedia.controller.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.service.announcer.AdService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdControllerTest {

    @InjectMocks
    private AdController adController;

    @Mock
    private  AdService adService;

    private AdPostDto adPostDto;
    private AdDto adDto;
    private long userId;
    private long idAd;
    private LocalDate startDate;
    private LocalDate endDate;
    private TotalAdsDto totalAdsDto;
    private AdUpdateDto adUpdateDto;

    @BeforeEach
    void setUp() {
        adPostDto = new AdPostDto(1L, "Content", "https://example.com/image.jpg", "https://example.com/video.mp4");
        adDto = new AdDto(1L, 1L, "Content", "https://example.com/image.jpg", "https://example.com/video.mp4", Instant.now(), LocalDateTime.now().plusDays(30), true, null);
        userId = 1L;
        startDate = LocalDate.now().minusDays(7);
        endDate = LocalDate.now();
        totalAdsDto = new TotalAdsDto(5, 3);
        adUpdateDto = new AdUpdateDto("Updated Content", "https://example.com/updated-image.jpg", "https://example.com/updated-video.mp4");
        adDto = new AdDto(1L, 1L, "Updated Content", "https://example.com/updated-image.jpg", "https://example.com/updated-video.mp4", Instant.now(), LocalDateTime.now().plusDays(30), true, null);
    }

    @Test
    void givenValidAdPostDtoAndUserId_whenCreateAd_thenReturnCreatedAd() {
        // Given
        when(adService.create(adPostDto, userId)).thenReturn(adDto);

        // When
        ResponseEntity<AdDto> response = adController.createAd(adPostDto, userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(adDto, response.getBody());
        verify(adService, times(1)).create(adPostDto, userId);
    }

    @Test
    void givenValidUserId_whenGetMyAds_thenReturnListOfAdDto() {
        // Given
        List<AdDto> adList = Arrays.asList(adDto);
        when(adService.findAllByUserId(userId)).thenReturn(adList);

        // When
        ResponseEntity<List<AdDto>> response = adController.getMyAds(userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adList, response.getBody());
        verify(adService, times(1)).findAllByUserId(userId);
    }

    @Test
    void givenNoAdsForUser_whenGetMyAds_thenReturnEmptyList() {
        // Given
        when(adService.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<AdDto>> response = adController.getMyAds(userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(adService, times(1)).findAllByUserId(userId);
    }

    @Test
    void givenValidUserIdAndDates_whenGetMyAdsActive_thenReturnListOfAdDto() {
        // Given
        List<AdDto> adList = Arrays.asList(adDto);
        when(adService.findAllActiveByUserId(startDate, endDate, userId)).thenReturn(adList);

        // When
        ResponseEntity<List<AdDto>> response = adController.getMyAdsActive(startDate, endDate, userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adList, response.getBody());
        verify(adService, times(1)).findAllActiveByUserId(startDate, endDate, userId);
    }

    @Test
    void givenNoActiveAdsForUser_whenGetMyAdsActive_thenReturnEmptyList() {
        // Given
        when(adService.findAllActiveByUserId(startDate, endDate, userId)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<AdDto>> response = adController.getMyAdsActive(startDate, endDate, userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(adService, times(1)).findAllActiveByUserId(startDate, endDate, userId);
    }

    @Test
    void givenNullDates_whenGetMyAdsActive_thenReturnListOfAdDto() {
        // Given
        List<AdDto> adList = Arrays.asList(adDto);
        when(adService.findAllActiveByUserId(null, null, userId)).thenReturn(adList);

        // When
        ResponseEntity<List<AdDto>> response = adController.getMyAdsActive(null, null, userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adList, response.getBody());
        verify(adService, times(1)).findAllActiveByUserId(null, null, userId);
    }

    @Test
    void givenValidId_whenDeactivateAd_thenReturnDeactivatedAdDto() {
        // Given
        when(adService.updateDeactivated(idAd)).thenReturn(adDto);

        // When
        ResponseEntity<AdDto> response = adController.deactivateAd(idAd);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adDto, response.getBody());
        verify(adService, times(1)).updateDeactivated(idAd);
    }

    @Test
    void givenInvalidId_whenDeactivateAd_thenThrowResponseStatusException() {
        // Given
        when(adService.updateDeactivated(idAd)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio no encontrado"));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> adController.deactivateAd(idAd));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Anuncio no encontrado", exception.getReason());
        verify(adService, times(1)).updateDeactivated(idAd);
    }

    @Test
    void givenValidIdAdPostDtoAndUserId_whenActivated_thenReturnActivatedAdDto() {
        // Given
        when(adService.updateActive(idAd, adPostDto, userId)).thenReturn(adDto);

        // When
        ResponseEntity<AdDto> response = adController.activated(idAd, adPostDto, userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adDto, response.getBody());
        verify(adService, times(1)).updateActive(idAd, adPostDto, userId);
    }

    @Test
    void givenInvalidId_whenActivated_thenThrowResponseStatusException() {
        // Given
        when(adService.updateActive(idAd, adPostDto, userId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio no encontrado"));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> adController.activated(idAd, adPostDto, userId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Anuncio no encontrado", exception.getReason());
        verify(adService, times(1)).updateActive(idAd, adPostDto, userId);
    }

    @Test
    void givenValidUserId_whenGetTotalAdsByUserId_thenReturnTotalAdsDto() {
        // Given
        when(adService.totalAdsByUserId(userId)).thenReturn(totalAdsDto);

        // When
        ResponseEntity<TotalAdsDto> response = adController.getTotalAdsByUserId(userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(totalAdsDto, response.getBody());
        verify(adService, times(1)).totalAdsByUserId(userId);
    }

    @Test
    void givenNoAdsForUser_whenGetTotalAdsByUserId_thenReturnTotalAdsDtoWithZeroValues() {
        // Given
        TotalAdsDto emptyTotalAdsDto = new TotalAdsDto(0, 0); // Sin anuncios
        when(adService.totalAdsByUserId(userId)).thenReturn(emptyTotalAdsDto);

        // When
        ResponseEntity<TotalAdsDto> response = adController.getTotalAdsByUserId(userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyTotalAdsDto, response.getBody());
        verify(adService, times(1)).totalAdsByUserId(userId);
    }

    @Test
    void givenUserId_whenGetAllPostAdMount_thenReturnListOfPostAdMount() {
        // Given
        List<PostAdMount> expectedPostAdMounts = List.of(
                new PostAdMount("January", 10),
                new PostAdMount("February", 5)
        );
        when(adService.getPostMount(userId)).thenReturn(expectedPostAdMounts);

        // When
        ResponseEntity<List<PostAdMount>> response = adController.getAllPostAdMount(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPostAdMounts, response.getBody());
    }

    @Test
    void givenUserId_whenGetAllPostAdMount_thenReturnEmptyList() {
        // Given
        List<PostAdMount> expectedPostAdMounts = Collections.emptyList();
        when(adService.getPostMount(userId)).thenReturn(expectedPostAdMounts);

        // When
        ResponseEntity<List<PostAdMount>> response = adController.getAllPostAdMount(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPostAdMounts, response.getBody());
    }

    @Test
    void givenIdAndAdUpdateDto_whenUpdate_thenReturnUpdatedAdDto() {
        // Given
        when(adService.update(idAd, adUpdateDto)).thenReturn(adDto);

        // When
        ResponseEntity<AdDto> response = adController.update(idAd, adUpdateDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adDto, response.getBody());
    }
}