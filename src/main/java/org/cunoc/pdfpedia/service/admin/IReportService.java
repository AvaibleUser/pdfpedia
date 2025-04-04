package org.cunoc.pdfpedia.service.admin;

import org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness.ReportAdvertiserAdViews;
import org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness.ViewsProjection;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.EarningsReport;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineCostTotalDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.AdReportEmailDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.PaymentPostAdPerAnnouncerDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.TotalReportPaymentPostAdByAnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.postAd.PostAdReportTotal;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.MagazineProjectionCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.ReportMagazineCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.MagazineProjectionDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.ReportTopMagazineSubscriptions;
import org.cunoc.pdfpedia.domain.dto.dashboard.CountRegisterByRolDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IReportService {

    List<CountRegisterByRolDto> findCountRegisterByRol(LocalDate startDate, LocalDate endDate);

    BigDecimal getTotalAdPost(List<AdReportDto> adPostReport);

    BigDecimal getTotalAdPostEmail(List<AdReportEmailDto> adPostReport);

    BigDecimal getTotalBlockAd(List<MagazineReportDto> blockAdReport);

    BigDecimal getCostTotal(List<MagazineCostTotalDto> costTotalReport);

    PostAdReportTotal getPostAdReportTotal(LocalDate startDate, LocalDate endDate, Integer type);

    TotalReportPaymentPostAdByAnnouncersDto mapperReport(List<PaymentPostAdPerAnnouncerDto> payments, List<AdReportEmailDto> adReports );

    TotalReportPaymentPostAdByAnnouncersDto getTotalPaymentsByAdvertisers();

    TotalReportPaymentPostAdByAnnouncersDto getTotalPaymentsByAdvertisers(LocalDate startDate, LocalDate endDate);

    TotalReportPaymentPostAdByAnnouncersDto getTotalPaymentsByAdvertisersById(Long id);

    TotalReportPaymentPostAdByAnnouncersDto getTotalPaymentsByAdvertisersById(LocalDate startDate, LocalDate endDate, Long id);

    ReportTopMagazineSubscriptions getTopClear(List<MagazineProjectionDto> subscriptions);

    ReportTopMagazineSubscriptions getTop5MagazinesBySubscriptions();

    ReportTopMagazineSubscriptions getTop5MagazinesBySubscriptionsRange(LocalDate startDate, LocalDate endDate);

    ReportMagazineCommentsDto getTopClearComments(List<MagazineProjectionCommentsDto> comments);

    ReportMagazineCommentsDto getTop5MagazinesByComments();

    ReportMagazineCommentsDto getTop5MagazinesByCommentsRange(LocalDate startDate, LocalDate endDate);

    EarningsReport getTotalReportEarnings(LocalDate startDate, LocalDate endDate);

    ReportAdvertiserAdViews getAdViewsReport(List<ViewsProjection> projections);

    ReportAdvertiserAdViews getAdViewsReportRange(LocalDate startDate, LocalDate endDate);
}
