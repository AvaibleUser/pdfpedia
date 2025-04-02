package org.cunoc.pdfpedia.service.dashboard;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.dashboard.ChartPlotDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.ExpiredAdBlockDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.StatsDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.SubscriptionsResumeDto;

public interface IEditorDashboardService {

    StatsDto getMonthlyStats(long editorId);

    List<ChartPlotDto> getMonthlyPerformance(long editorId);

    List<ChartPlotDto> getTopMagazines(long editorId);

    SubscriptionsResumeDto getSubscriptionsResume(long editorId);

    List<ExpiredAdBlockDto> getExpiredAdsBlock(long editorId);
}
