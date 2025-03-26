package org.cunoc.pdfpedia.service.monetary;


import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineReportDto;
import org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.type.PaymentType;
import org.cunoc.pdfpedia.repository.monetary.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public AdReportDto toDtoAdReport(PaymentEntity paymentEntity) {
        return AdReportDto
                .builder()
                .adType(paymentEntity.getAd().getChargePeriodAd().getAdType())
                .plan(paymentEntity.getAd().getChargePeriodAd().getDurationDays())
                .datePay(paymentEntity.getPaidAt())
                .username(paymentEntity.getAd().getAdvertiser().getUsername())
                .amount(paymentEntity.getAmount())
                .build();
    }


    public MagazineReportDto toDtoMagazineReport(PaymentEntity paymentEntity) {
        return MagazineReportDto
                .builder()
                .amount(paymentEntity.getAmount())
                .datePay(paymentEntity.getPaidAt())
                .editor(paymentEntity.getMagazine().getEditor().getUsername())
                .title(paymentEntity.getMagazine().getTitle())
                .build();
    }


    @Transactional
    public void createPaymentPostAd(BigDecimal amount, AdEntity ad){
        PaymentEntity paymentEntity = PaymentEntity
                .builder()
                .amount(amount)
                .ad(ad)
                .paymentType(PaymentType.POST_AD)
                .build();

        paymentRepository.save(paymentEntity);
    }

    public List<TotalAmountPaymentByMonthDto> getSumAmountPostAdsByMount(Long userId) {
        return this.paymentRepository.sumAmountAdsByMonth(userId);
    }

    @Transactional
    public List<AdReportDto> getPaymentToPostAdBetween(LocalDate startDate, LocalDate endDate){

        if (startDate == null || endDate == null) {
            return paymentRepository
                    .findByPaymentType(PaymentType.POST_AD)
                    .stream()
                    .map(this::toDtoAdReport).toList();

        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        return paymentRepository
                .findByPaymentTypeAndDateRange(PaymentType.POST_AD, startInstant, endInstant)
                .stream()
                .map(this::toDtoAdReport).toList();

    }

    @Transactional
    public List<MagazineReportDto> getPaymentToBlockAdMagazineBetween(LocalDate startDate, LocalDate endDate){

        if (startDate == null || endDate == null) {
            return paymentRepository
                    .findByPaymentTypeMagazine(PaymentType.BLOCK_ADS)
                    .stream()
                    .map(this::toDtoMagazineReport).toList();

        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        return paymentRepository
                .findByPaymentTypeMagazineAndDateRange(PaymentType.BLOCK_ADS, startInstant, endInstant)
                .stream()
                .map(this::toDtoMagazineReport).toList();

    }
}
