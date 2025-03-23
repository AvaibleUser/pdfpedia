package org.cunoc.pdfpedia.service.monetary;

import org.cunoc.pdfpedia.domain.dto.announcer.AdPostDto;
import org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.type.PaymentType;
import org.cunoc.pdfpedia.repository.monetary.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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


    @BeforeEach
    void setUp(){

        advertiser = new UserEntity();
        advertiser.setId(1L);
        chargePeriodAd = new ChargePeriodAdEntity();
        chargePeriodAd.setDurationDays(2);
        chargePeriodAd.setId(1L);
        adEntity = AdEntity
                .builder()
                .id(1l)
                .advertiser(advertiser)
                .chargePeriodAd(chargePeriodAd)
                .content("Content").imageUrl("https://example")
                .videoUrl("https://example")
                .expiresAt(LocalDateTime.now().plusDays(7))
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


}