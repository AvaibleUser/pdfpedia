package org.cunoc.pdfpedia.controller.admin;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness.ReportAdvertiserAdViews;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.EarningsReport;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.TotalReportPaymentPostAdByAnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.postAd.PostAdReportTotal;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.ReportMagazineCommentsDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.ReportTopMagazineSubscriptions;
import org.cunoc.pdfpedia.domain.dto.dashboard.CountRegisterByRolDto;
import org.cunoc.pdfpedia.service.admin.IReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    @GetMapping("/count-by-role")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<CountRegisterByRolDto>> getCountByRole(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(this.reportService.findCountRegisterByRol(startDate, endDate));
    }

    @GetMapping("/earnings-total")
    @RolesAllowed("ADMIN")
    public ResponseEntity<EarningsReport> getTotalReportEarnings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.reportService.getTotalReportEarnings(startDate, endDate));
    }

    @GetMapping("/post-ad-total")
    @RolesAllowed("ADMIN")
    public ResponseEntity<PostAdReportTotal> getTotalReportEarnings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam Integer type
    ){
        return ResponseEntity.ok(this.reportService.getPostAdReportTotal(startDate, endDate, type));
    }

    @GetMapping("/post-ad-total-announcers")
    @RolesAllowed("ADMIN")
    public ResponseEntity<TotalReportPaymentPostAdByAnnouncersDto> getTotalReportEarningsPostAdByAnnouncers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.reportService.getTotalPaymentsByAdvertisers(startDate, endDate));
    }

    @GetMapping("/post-ad-total-announcers/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<TotalReportPaymentPostAdByAnnouncersDto> getTotalReportEarningsPostAdByAnnouncersById(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PathVariable Long id
    ){
        return ResponseEntity.ok(this.reportService.getTotalPaymentsByAdvertisersById(startDate, endDate,id));
    }

    @GetMapping("/top-5-magazines-subscription")
    @RolesAllowed("ADMIN")
    public ResponseEntity<ReportTopMagazineSubscriptions> getTop5MagazinesBySubscriptions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.reportService.getTop5MagazinesBySubscriptionsRange(startDate, endDate));
    }

    @GetMapping("/top-5-magazines-comments")
    @RolesAllowed("ADMIN")
    public ResponseEntity<ReportMagazineCommentsDto> getTop5MagazinesByComments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.reportService.getTop5MagazinesByCommentsRange(startDate, endDate));
    }

    @GetMapping("/ad_effectiveness")
    @RolesAllowed("ADMIN")
    public ResponseEntity<ReportAdvertiserAdViews> getAdEffectivenessByViews(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.reportService.getAdViewsReportRange(startDate, endDate));
    }

}
