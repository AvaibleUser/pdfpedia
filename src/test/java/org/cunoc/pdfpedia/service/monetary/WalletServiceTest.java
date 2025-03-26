package org.cunoc.pdfpedia.service.monetary;

import org.cunoc.pdfpedia.domain.dto.monetary.WalletDto;
import org.cunoc.pdfpedia.domain.entity.monetary.WalletEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.BadRequestException;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.monetary.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    private WalletEntity walletEntity;

    @BeforeEach
    void setUp(){
        walletEntity = WalletEntity.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(1000))
                .isDeleted(false)
                .user(new UserEntity())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /**
     * test function toDto
     */
    @Test
    void givenWalletEntity_whenToDto_thenReturnWalletDto() {
        // Given
        WalletDto expectedDto = new WalletDto(walletEntity.getId(), walletEntity.getBalance());

        // When
        WalletDto result = walletService.toDto(walletEntity);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.balance(), result.balance());
    }

    /**
     * tests function findUserById
     */
    @Test
    void givenValidUserId_whenFindUserById_thenReturnWalletDto() {
        // Given
        Long userId = 1L;
        when(walletRepository.findAllByUserId(userId)).thenReturn(Optional.of(walletEntity));

        WalletDto expectedDto = new WalletDto(walletEntity.getId(), walletEntity.getBalance());

        // When
        WalletDto result = walletService.findUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.balance(), result.balance());
        verify(walletRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void givenInvalidUserId_whenFindUserById_thenThrowValueNotFoundException() {
        // Given
        Long userId = 1L;
        when(walletRepository.findAllByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValueNotFoundException.class, () -> walletService.findUserById(userId));
        verify(walletRepository, times(1)).findAllByUserId(userId);
    }

    /**
     * tests function updateIncrease
     */
    @Test
    void givenValidIdAndWalletDto_whenUpdateIncrease_thenReturnUpdatedWalletDto() {
        // Given
        Long id = 1L;
        WalletDto dto = new WalletDto(1L, BigDecimal.valueOf(500));

        when(walletRepository.findById(id)).thenReturn(Optional.of(walletEntity));
        when(walletRepository.save(walletEntity)).thenReturn(walletEntity);

        WalletDto expectedDto = new WalletDto(walletEntity.getId(), walletEntity.getBalance().add(dto.balance()));

        // When
        WalletDto result = walletService.updateIncrease(id, dto);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.balance(), result.balance());
        verify(walletRepository, times(1)).findById(id);
        verify(walletRepository, times(1)).save(walletEntity);
    }

    @Test
    void givenInvalidId_whenUpdateIncrease_thenThrowValueNotFoundException() {
        // Given
        Long id = 1L;
        WalletDto dto = new WalletDto(1L, BigDecimal.valueOf(500));

        when(walletRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValueNotFoundException.class, () -> walletService.updateIncrease(id, dto));
        verify(walletRepository, times(1)).findById(id);
        verify(walletRepository, never()).save(any(WalletEntity.class));
    }

    /**
     * tests function updateDecrease(Long userId, BigDecimal balance)
     */
    @Test
    void givenValidUserIdAndSufficientBalance_whenUpdateDecrease_thenBalanceIsDecreased() {
        // Given
        Long userId = 1L;
        BigDecimal balanceToDecrease = BigDecimal.valueOf(500);

        when(walletRepository.findAllByUserId(userId)).thenReturn(Optional.of(walletEntity));

        // When
        walletService.updateDecrease(userId, balanceToDecrease);

        // Then
        assertEquals(BigDecimal.valueOf(500), walletEntity.getBalance());
        verify(walletRepository, times(1)).findAllByUserId(userId);
        verify(walletRepository, times(1)).save(walletEntity);
    }

    @Test
    void givenInvalidUserId_whenUpdateDecrease_thenThrowValueNotFoundException() {
        // Given
        Long userId = 1L;
        BigDecimal balanceToDecrease = BigDecimal.valueOf(500);

        when(walletRepository.findAllByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ValueNotFoundException.class, () -> walletService.updateDecrease(userId, balanceToDecrease));
        verify(walletRepository, times(1)).findAllByUserId(userId);
        verify(walletRepository, never()).save(any(WalletEntity.class));
    }

    @Test
    void givenInsufficientBalance_whenUpdateDecrease_thenThrowBadRequestException() {
        // Given
        Long userId = 1L;
        BigDecimal balanceToDecrease = BigDecimal.valueOf(1500);

        when(walletRepository.findAllByUserId(userId)).thenReturn(Optional.of(walletEntity));

        // When & Then
        assertThrows(BadRequestException.class, () -> walletService.updateDecrease(userId, balanceToDecrease));
        verify(walletRepository, times(1)).findAllByUserId(userId);
        verify(walletRepository, never()).save(any(WalletEntity.class));
    }

}