package org.cunoc.pdfpedia.controller.admin.export;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.EarningsReport;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.TotalReportPaymentPostAdByAnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.postAd.PostAdReportTotal;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.ReportMagazineCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.ReportTopMagazineSubscriptions;
import org.cunoc.pdfpedia.service.admin.export.ExportReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/v1/admin/exports")
@RequiredArgsConstructor
public class ExportReportController {

    private final ExportReportService exportReportService;

    @PostMapping("/earning")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Resource> exportReportEarnings(@RequestBody EarningsReport earningsReport){
        return this.exportReportService.exportReportEarningsTotal(earningsReport);
    }

    @PostMapping("/post-ad")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Resource> exportReportPostAd(@RequestBody PostAdReportTotal postAdReportTotal){
        return this.exportReportService.exportReportPostAd(postAdReportTotal);
    }

    @PostMapping("/announcers-post-ad")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Resource> exportReportAnnouncersPostAd(@RequestBody TotalReportPaymentPostAdByAnnouncersDto dto){
        return this.exportReportService.exportReportAnnouncersPostAd(dto);
    }

    @PostMapping("/top-5-magazine-subscriptions")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Resource> exportReportTop5MagazineSubscriptions(@RequestBody ReportTopMagazineSubscriptions dto){
        return this.exportReportService.exportReportTop5MagazineSubscriptions(dto);
    }

    @PostMapping("/top-5-magazine-comments")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Resource> exportReportTop5MagazineComments(@RequestBody ReportMagazineCommentsDto dto){
        return this.exportReportService.exportReportTop5MagazineComments(dto);
    }
}
