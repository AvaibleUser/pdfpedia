package org.cunoc.pdfpedia.controller.admin;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.EarningsReport;
import org.cunoc.pdfpedia.domain.dto.admin.report.postAd.PostAdReportTotal;
import org.cunoc.pdfpedia.domain.dto.dashboard.CountRegisterByRolDto;
import org.cunoc.pdfpedia.service.admin.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/count-by-role")
    public ResponseEntity<List<CountRegisterByRolDto>> getCountByRole(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(this.reportService.findCountRegisterByRol(startDate, endDate));
    }

    @GetMapping("/earnings-total")
    public ResponseEntity<EarningsReport> getTotalReportEarnings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.reportService.getTotalReportEarnings(startDate, endDate));
    }

    @GetMapping("/post-ad-total")
    public ResponseEntity<PostAdReportTotal> getTotalReportEarnings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam Integer type
    ){
        return ResponseEntity.ok(this.reportService.getPostAdReportTotal(startDate, endDate, type));
    }


}
