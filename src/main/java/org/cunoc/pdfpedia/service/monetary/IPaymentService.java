package org.cunoc.pdfpedia.service.monetary;

import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineReportDto;
import org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.type.AdType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IPaymentService {

    AdReportDto toDtoAdReport(PaymentEntity paymentEntity);

    MagazineReportDto toDtoMagazineReport(PaymentEntity paymentEntity);

    void createPaymentPostAd(BigDecimal amount, AdEntity ad);

    List<TotalAmountPaymentByMonthDto> getSumAmountPostAdsByMount(Long userId);

    List<AdReportDto> getPaymentToPostAdBetween(LocalDate startDate, LocalDate endDate);

    List<MagazineReportDto> getPaymentToBlockAdMagazineBetween(LocalDate startDate, LocalDate endDate);

    AdType getTypeFilter(Integer type);

    List<AdReportDto> getPaymentToPostAdByTypeAndBetween(LocalDate startDate, LocalDate endDate, Integer type);

}
