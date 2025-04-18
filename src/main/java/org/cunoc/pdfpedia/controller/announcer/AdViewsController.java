package org.cunoc.pdfpedia.controller.announcer;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.AdViewCreateDto;
import org.cunoc.pdfpedia.domain.dto.announcer.AdViewReportDto;
import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalViewsAdDto;
import org.cunoc.pdfpedia.service.announcer.IAdViewsService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/ads/views")
@RequiredArgsConstructor
public class AdViewsController {

    private final IAdViewsService adViewsService;

    @PostMapping
    @ResponseStatus(CREATED)
    public void addAdViews(@Valid @RequestBody AdViewCreateDto adViewCreateDto) {
        this.adViewsService.create(adViewCreateDto);
    }

    @GetMapping("/total")
    @RolesAllowed("ANNOUNCER")
    public ResponseEntity<TotalViewsAdDto> getTotal(@CurrentUserId long userId) {
       return ResponseEntity.status(HttpStatus.OK).body(this.adViewsService.getTotalViews(userId));
    }

    @GetMapping("/views-count-mount")
    @RolesAllowed("ANNOUNCER")
    public ResponseEntity<List<PostAdMount>> getAllPostAdMount(@CurrentUserId long userId){
        return ResponseEntity.status(HttpStatus.OK).body(this.adViewsService.getPostMount(userId));
    }

    @GetMapping("/report-views")
    @RolesAllowed("ANNOUNCER")
    public ResponseEntity<List<AdViewReportDto>> getReportViews(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @CurrentUserId long userId) {

        List<AdViewReportDto> report = adViewsService.getReportViews(startDate, endDate, userId);
        return ResponseEntity.ok(report);
    }
}
