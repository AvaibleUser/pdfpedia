package org.cunoc.pdfpedia.controller.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.ChargePeriodAdDto;
import org.cunoc.pdfpedia.domain.dto.announcer.CountAdByTypeDto;
import org.cunoc.pdfpedia.domain.type.AdType;
import org.cunoc.pdfpedia.service.announcer.ChargePeriodAdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class ChargePeriodAdControllerTest {

    @InjectMocks
    private ChargePeriodAdController chargePeriodAdController;

    @Mock
    private ChargePeriodAdService chargePeriodAdService;

    private List<ChargePeriodAdDto> chargePeriodAdDtos;
    private ChargePeriodAdDto chargePeriodAdDto;
    private Long id;
    private long userId;
    private List<CountAdByTypeDto> countAdByTypeDtos;

    @BeforeEach
    void setUp() {
        chargePeriodAdDtos = List.of(
                new ChargePeriodAdDto(1L, AdType.TEXT, 30, BigDecimal.valueOf(100.0)),
                new ChargePeriodAdDto(2L, AdType.TEXT_IMAGE, 60, BigDecimal.valueOf(200.0))
        );
        id = 1L;
        chargePeriodAdDto = new ChargePeriodAdDto(id, AdType.TEXT, 30, BigDecimal.valueOf(100.0));
        userId = 1L;
        countAdByTypeDtos = List.of(
                new CountAdByTypeDto(AdType.TEXT, 10),
                new CountAdByTypeDto(AdType.TEXT_IMAGE, 5)
        );
    }

    @Test
    void givenChargePeriodAdsExist_whenGetAll_thenReturnListOfChargePeriodAdDto() {
        // Given
        when(chargePeriodAdService.findAll()).thenReturn(chargePeriodAdDtos);

        // When
        ResponseEntity<List<ChargePeriodAdDto>> response = chargePeriodAdController.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chargePeriodAdDtos, response.getBody());
    }

    @Test
    void givenNoChargePeriodAdsExist_whenGetAll_thenReturnEmptyList() {
        // Given
        when(chargePeriodAdService.findAll()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<ChargePeriodAdDto>> response = chargePeriodAdController.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void givenIdAndChargePeriodAdDto_whenUpdate_thenReturnUpdatedChargePeriodAdDto() {
        // Given
        when(chargePeriodAdService.update(id, chargePeriodAdDto)).thenReturn(chargePeriodAdDto);

        // When
        ResponseEntity<ChargePeriodAdDto> response = chargePeriodAdController.update(id, chargePeriodAdDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chargePeriodAdDto, response.getBody());
    }

    @Test
    void givenUserId_whenGetAllPostAdMount_thenReturnListOfCountAdByTypeDto() {
        // Given
        when(chargePeriodAdService.getAdCountsByTypeForUser(userId)).thenReturn(countAdByTypeDtos);

        // When
        ResponseEntity<List<CountAdByTypeDto>> response = chargePeriodAdController.getAllPostAdMount(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(countAdByTypeDtos, response.getBody());
    }

    @Test
    void givenUserId_whenGetAllPostAdMount_thenReturnEmptyList() {
        // Given
        when(chargePeriodAdService.getAdCountsByTypeForUser(userId)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<CountAdByTypeDto>> response = chargePeriodAdController.getAllPostAdMount(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

}