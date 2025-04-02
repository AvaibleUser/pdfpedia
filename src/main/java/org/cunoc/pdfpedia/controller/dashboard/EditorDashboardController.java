package org.cunoc.pdfpedia.controller.dashboard;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.dashboard.ChartPlotDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.ExpiredAdBlockDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.StatsDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.SubscriptionsResumeDto;
import org.cunoc.pdfpedia.service.dashboard.IEditorDashboardService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/editors/dashboard")
@RequiredArgsConstructor
public class EditorDashboardController {

    private final IEditorDashboardService editorDashboardService;

    @GetMapping(params = "type=stats")
    @RolesAllowed("EDITOR")
    public StatsDto getMonthlyStats(@CurrentUserId @Positive long editorId) {
        return editorDashboardService.getMonthlyStats(editorId);
    }

    @GetMapping(params = "type=performance")
    @RolesAllowed("EDITOR")
    public List<ChartPlotDto> getMonthlyPerformance(@CurrentUserId @Positive long editorId) {
        return editorDashboardService.getMonthlyPerformance(editorId);
    }

    @GetMapping(params = "type=top-magazines")
    @RolesAllowed("EDITOR")
    public List<ChartPlotDto> getTopMagazines(@CurrentUserId @Positive long editorId) {
        return editorDashboardService.getTopMagazines(editorId);
    }

    @GetMapping(params = "type=subscriptions-resume")
    @RolesAllowed("EDITOR")
    public SubscriptionsResumeDto getSubscriptionsResume(@CurrentUserId @Positive long editorId) {
        return editorDashboardService.getSubscriptionsResume(editorId);
    }

    @GetMapping(params = "type=expired-ad-blocks")
    @RolesAllowed("EDITOR")
    public List<ExpiredAdBlockDto> getExpiredAdsBlock(@CurrentUserId @Positive long editorId) {
        return editorDashboardService.getExpiredAdsBlock(editorId);
    }
}
