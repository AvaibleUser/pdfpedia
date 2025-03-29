package org.cunoc.pdfpedia.service.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.ChargePeriodAdDto;
import org.cunoc.pdfpedia.domain.dto.announcer.CountAdByTypeDto;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.domain.type.AdType;
import org.cunoc.pdfpedia.repository.announcer.ChargePeriodAdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargePeriodAdServiceTest {

    @InjectMocks
    private ChargePeriodAdService chargePeriodAdService;

    @Mock
    private ChargePeriodAdRepository chargePeriodAdRepository;

    private ChargePeriodAdEntity chargePeriodAdEntity;
    private ChargePeriodAdDto chargePeriodAdDto;

    @BeforeEach
    void setUp(){

        chargePeriodAdEntity = ChargePeriodAdEntity
                .builder()
                .id(1L)
                .durationDays(30)
                .cost(BigDecimal.valueOf(100))
                .adType(AdType.TEXT)
                .build();

        chargePeriodAdDto = new ChargePeriodAdDto(
                1L,
                AdType.VIDEO,
                60,
                BigDecimal.valueOf(200)
        );

    }

    /**
     * test function toDto
     */
    @Test
    void givenChargePeriodAdEntity_whenToDto_thenReturnChargePeriodAdDto() {
        // Given
        ChargePeriodAdDto expectedDto = new ChargePeriodAdDto(
                chargePeriodAdEntity.getId(),
                chargePeriodAdEntity.getAdType(),
                chargePeriodAdEntity.getDurationDays(),
                chargePeriodAdEntity.getCost()
        );

        // When
        ChargePeriodAdDto result = chargePeriodAdService.toDto(chargePeriodAdEntity);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.adType(), result.adType());
        assertEquals(expectedDto.durationDays(), result.durationDays());
        assertEquals(expectedDto.cost(), result.cost());
    }


    /**
     * test function findAll
     */
    @Test
    void givenChargePeriodAdsExist_whenFindAll_thenReturnListOfChargePeriodAdDto() {
        // Given
        List<ChargePeriodAdEntity> chargePeriodAdEntities = Arrays.asList(chargePeriodAdEntity);
        when(chargePeriodAdRepository.findAll()).thenReturn(chargePeriodAdEntities);

        ChargePeriodAdDto expectedDto = new ChargePeriodAdDto(
                chargePeriodAdEntity.getId(),
                chargePeriodAdEntity.getAdType(),
                chargePeriodAdEntity.getDurationDays(),
                chargePeriodAdEntity.getCost()
        );

        // When
        List<ChargePeriodAdDto> result = chargePeriodAdService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDto, result.get(0));
        verify(chargePeriodAdRepository, times(1)).findAll();
    }

    @Test
    void givenNoChargePeriodAdsExist_whenFindAll_thenReturnEmptyList() {
        // Given
        when(chargePeriodAdRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ChargePeriodAdDto> result = chargePeriodAdService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chargePeriodAdRepository, times(1)).findAll();
    }

    /**
     * test function update
     */
    @Test
    void givenValidIdAndChargePeriodAdDto_whenUpdate_thenReturnUpdatedChargePeriodAdDto() {
        // Given
        when(chargePeriodAdRepository.findById(1L)).thenReturn(Optional.of(chargePeriodAdEntity));
        when(chargePeriodAdRepository.save(chargePeriodAdEntity)).thenReturn(chargePeriodAdEntity);

        // When
        ChargePeriodAdDto result = chargePeriodAdService.update(1L, chargePeriodAdDto);

        // Then
        assertNotNull(result);
        assertEquals(chargePeriodAdDto.id(), result.id());
        assertEquals(chargePeriodAdDto.durationDays(), result.durationDays()+30);
        assertEquals(chargePeriodAdDto.cost(), result.cost());
        verify(chargePeriodAdRepository, times(1)).findById(1L);
        verify(chargePeriodAdRepository, times(1)).save(chargePeriodAdEntity);
    }

    @Test
    void givenInvalidId_whenUpdate_thenThrowValueNotFoundException() {
        // Given
        when(chargePeriodAdRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValueNotFoundException.class, () -> chargePeriodAdService.update(1L, chargePeriodAdDto));
        verify(chargePeriodAdRepository, times(1)).findById(1L);
        verify(chargePeriodAdRepository, never()).save(any(ChargePeriodAdEntity.class));
    }

    /**
     * test function getAdCountsByTypeForUser
     */
    @Test
    void givenValidAdvertiserId_whenGetAdCountsByTypeForUser_thenReturnListOfCountAdByTypeDto() {
        // Given
        Long advertiserId = 1L;
        List<CountAdByTypeDto> expectedCounts = Arrays.asList(
                new CountAdByTypeDto(AdType.TEXT, 5),
                new CountAdByTypeDto(AdType.VIDEO, 3)
        );

        when(chargePeriodAdRepository.countAdsByTypeForUser(advertiserId)).thenReturn(expectedCounts);

        // When
        List<CountAdByTypeDto> result = chargePeriodAdService.getAdCountsByTypeForUser(advertiserId);

        // Then
        assertNotNull(result);
        assertEquals(expectedCounts.size(), result.size());
        assertEquals(expectedCounts, result);
        verify(chargePeriodAdRepository, times(1)).countAdsByTypeForUser(advertiserId);
    }

    @Test
    void givenNoAdsForUser_whenGetAdCountsByTypeForUser_thenReturnEmptyList() {
        // Given
        Long advertiserId = 1L;
        when(chargePeriodAdRepository.countAdsByTypeForUser(advertiserId)).thenReturn(Collections.emptyList());

        // When
        List<CountAdByTypeDto> result = chargePeriodAdService.getAdCountsByTypeForUser(advertiserId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chargePeriodAdRepository, times(1)).countAdsByTypeForUser(advertiserId);
    }

}