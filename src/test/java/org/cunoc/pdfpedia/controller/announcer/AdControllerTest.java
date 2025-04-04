package org.cunoc.pdfpedia.controller.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.domain.dto.dashboard.AnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.magazine.TopEditorDto;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



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
    private TotalTarjertDto totalTarjertDto;
    private TopEditorDto topEditorDto;
    private List<PostAdMount> postAdMountList;
    private List<AdDto> ads;


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
        totalTarjertDto = new TotalTarjertDto(10L);
        topEditorDto = new TopEditorDto("JohnDoe");
        postAdMountList = List.of(
                new PostAdMount("January", 5),
                new PostAdMount("February", 10)
        );
        AdDto adDto1 = new AdDto(1L, 1L, "Content 1", "https://example.com/image1.jpg", "https://example.com/video1.mp4", Instant.now(), LocalDateTime.now().plusDays(30), true, null);
        AdDto adDto2 = new AdDto(2L, 2L, "Content 2", "https://example.com/image2.jpg", "https://example.com/video2.mp4", Instant.now(), LocalDateTime.now().plusDays(30), false, null);
        ads = List.of(adDto1, adDto2);

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

    /**
     * tests Function totalPostAd
     */
    @Test
    void shouldReturnTotalPostAd_WhenValidDatesProvided() {
        // Given
        when(adService.getTotalPostAd(startDate, endDate)).thenReturn(totalTarjertDto);

        // When
        ResponseEntity<TotalTarjertDto> response = adController.totalPostAd(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(totalTarjertDto, response.getBody());
        verify(adService, times(1)).getTotalPostAd(startDate, endDate);
    }

    @Test
    void shouldReturnTotalPostAd_WhenNoDatesProvided() {
        // Given
        when(adService.getTotalPostAd(null, null)).thenReturn(totalTarjertDto);

        // When
        ResponseEntity<TotalTarjertDto> response = adController.totalPostAd(null, null);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(totalTarjertDto, response.getBody());
        verify(adService, times(1)).getTotalPostAd(null, null);
    }

    /**
     * tests function getTopEditorInRange
     */
    @Test
    void shouldReturnTopEditorWhenDatesAreProvided() {
        // Given
        when(adService.getTopPostAd(startDate, endDate)).thenReturn(topEditorDto);

        // When
        ResponseEntity<TopEditorDto> response = adController.getTopEditorInRange(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("JohnDoe", response.getBody().userName());
        verify(adService, times(1)).getTopPostAd(startDate, endDate);
    }

    @Test
    void shouldReturnTopEditorWhenDatesAreNull() {
        // Given
        when(adService.getTopPostAd(null, null)).thenReturn(topEditorDto);

        // When
        ResponseEntity<TopEditorDto> response = adController.getTopEditorInRange(null, null);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("JohnDoe", response.getBody().userName());
        verify(adService, times(1)).getTopPostAd(null, null);
    }

    /**
     * tests function getAdCountsByMonth
     */
    @Test
    void shouldReturnAdCountsByMonthWhenDatesAreProvided() {
        // Given
        when(adService.getAdCountsByMonth(startDate, endDate)).thenReturn(postAdMountList);

        // When
        ResponseEntity<List<PostAdMount>> response = adController.getAdCountsByMonth(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postAdMountList, response.getBody());
        verify(adService).getAdCountsByMonth(startDate, endDate);
    }

    @Test
    void shouldReturnAdCountsByMonthWhenDatesAreNull() {
        // Given
        when(adService.getAdCountsByMonth(null, null)).thenReturn(postAdMountList);

        // When
        ResponseEntity<List<PostAdMount>> response = adController.getAdCountsByMonth(null, null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postAdMountList, response.getBody());
        verify(adService).getAdCountsByMonth(null, null);
    }

    @Test
    void shouldReturnEmptyListWhenNoAdsExist() {
        // Given
        when(adService.getAdCountsByMonth(startDate, endDate)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<PostAdMount>> response = adController.getAdCountsByMonth(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(adService).getAdCountsByMonth(startDate, endDate);
    }

    /**
     * tests function findAll
     */
    @Test
    void givenAds_whenFindAll_thenReturnsAdList() {
        // Given
        when(adService.findAll()).thenReturn(ads);

        // When
        ResponseEntity<List<AdDto>> response = adController.findAll();

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void givenNoAds_whenFindAll_thenReturnsEmptyList() {
        // Given
        when(adService.findAll()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<AdDto>> response = adController.findAll();

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
    }

    /**
     * tests function findAllAnnouncers
     */
    @Test
    void givenAnnouncers_whenFindAllAnnouncers_thenReturnsAnnouncerList() {
        // Given
        List<AnnouncersDto> mockAnnouncers = List.of(
                new AnnouncersDto(1L, "JohnDoe"),
                new AnnouncersDto(2L, "JaneDoe")
        );
        when(adService.findAllAnnouncers()).thenReturn(mockAnnouncers);

        // When
        ResponseEntity<List<AnnouncersDto>> response = adController.findAllAnnouncers();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(adService, times(1)).findAllAnnouncers();
    }

    @Test
    void givenNoAnnouncers_whenFindAllAnnouncers_thenReturnsEmptyList() {
        // Given
        when(adService.findAllAnnouncers()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<AnnouncersDto>> response = adController.findAllAnnouncers();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(adService, times(1)).findAllAnnouncers();
    }

    /**
     * tests function getMyAds
     */
    @Test
    void givenAdsWhenGetMyAdsThenReturnOk() {
        // given
        when(adService.findAllByUserId(userId)).thenReturn(ads);

        // when
        ResponseEntity<List<AdDto>> response = adController.getMyAdsById(userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ads);
        verify(adService).findAllByUserId(userId);
    }

    @Test
    void givenNoAdsWhenGetMyAdsThenReturnOkWithEmptyList() {
        // given
        when(adService.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // when
        ResponseEntity<List<AdDto>> response = adController.getMyAdsById(userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isEmpty()).isTrue();
        verify(adService).findAllByUserId(userId);
    }

    /**
     * tests function getRandomAdd
     */
    @Test
    void givenAdWhenGetRandomAddThenReturnAd() {
        // given
        when(adService.getRandomAdd()).thenReturn(adDto);

        // when
        AdDto response = adController.getRandomAdd();

        // then
        assertThat(response).isEqualTo(adDto);
        verify(adService).getRandomAdd();
    }

    @Test
    void givenNoAdWhenGetRandomAddThenReturnNull() {
        // given
        when(adService.getRandomAdd()).thenReturn(null);

        // when
        AdDto response = adController.getRandomAdd();

        // then
        assertThat(response).isNull();
        verify(adService).getRandomAdd();
    }


}