package org.cunoc.pdfpedia.service.admin.export;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.EarningsReport;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.TotalReportPaymentPostAdByAnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.postAd.PostAdReportTotal;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.ReportMagazineCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.ReportTopMagazineSubscriptions;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExportReportService {

    private final PdfService pdfService;

    public ResponseEntity<Resource> exportReportEarningsTotal(EarningsReport earningsReport) {
        Map<String, Object> templateVariables = new HashMap<>();

        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("range", earningsReport.range());
        templateVariables.put("report", earningsReport);
        templateVariables.put("companyLogo", "https://res.cloudinary.com/ddkp3bobz/image/upload/v1742243659/Yc9SIViERyKTTCs71iDz-g_hfvelz.webp");

        return this.pdfService.downloadPdf("report-earnings-total",templateVariables);

    }

    public ResponseEntity<Resource> exportReportPostAd(PostAdReportTotal postAdReportTotal) {
        Map<String, Object> templateVariables = new HashMap<>();

        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("range", postAdReportTotal.range());
        templateVariables.put("report", postAdReportTotal.adReportDto());
        templateVariables.put("totalAdPost", postAdReportTotal.totalAdPost());
        templateVariables.put("filter", postAdReportTotal.filter());
        templateVariables.put("companyLogo", "https://res.cloudinary.com/ddkp3bobz/image/upload/v1742243659/Yc9SIViERyKTTCs71iDz-g_hfvelz.webp");

        return this.pdfService.downloadPdf("report-post-ad",templateVariables);

    }


    public ResponseEntity<Resource> exportReportAnnouncersPostAd(TotalReportPaymentPostAdByAnnouncersDto dto) {
        Map<String, Object> templateVariables = new HashMap<>();

        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("range", dto.range());
        templateVariables.put("report", dto.paymentPostAdPerAnnouncerDtos());
        templateVariables.put("totalAdPost", dto.totalAdPost());
        templateVariables.put("filter", dto.filter());
        templateVariables.put("companyLogo", "https://res.cloudinary.com/ddkp3bobz/image/upload/v1742243659/Yc9SIViERyKTTCs71iDz-g_hfvelz.webp");

        return this.pdfService.downloadPdf("report-announcers-post-ad",templateVariables);

    }

    public ResponseEntity<Resource> exportReportTop5MagazineSubscriptions(ReportTopMagazineSubscriptions dto) {
        Map<String, Object> templateVariables = new HashMap<>();

        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("range", dto.range());
        templateVariables.put("subscriptions", dto.subscriptions());
        templateVariables.put("companyLogo", "https://res.cloudinary.com/ddkp3bobz/image/upload/v1742243659/Yc9SIViERyKTTCs71iDz-g_hfvelz.webp");

        return this.pdfService.downloadPdf("report-subscriptions-magazine",templateVariables);

    }


    public ResponseEntity<Resource> exportReportTop5MagazineComments(ReportMagazineCommentsDto dto) {
        Map<String, Object> templateVariables = new HashMap<>();

        templateVariables.put("dateReport", LocalDate.now());
        templateVariables.put("range", dto.range());
        templateVariables.put("magazineCommentsDtoList", dto.magazineCommentsDtoList());
        templateVariables.put("companyLogo", "https://res.cloudinary.com/ddkp3bobz/image/upload/v1742243659/Yc9SIViERyKTTCs71iDz-g_hfvelz.webp");

        return this.pdfService.downloadPdf("report-magazine-comments",templateVariables);

    }



}
