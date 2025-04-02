package org.cunoc.pdfpedia.service.dashboard;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.dashboard.ChartPlotDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.ExpiredAdBlockDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.StatsDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.SubscriptionsResumeDto;
import org.cunoc.pdfpedia.repository.dashboard.DashboardRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EditorDashboardService implements IEditorDashboardService {

    private final UserRepository userRepository;
    private final DashboardRepository dashboardRepository;

    @Override
    public StatsDto getMonthlyStats(long editorId) {
        if (!userRepository.existsById(editorId)) {
            throw new BadCredentialsException("El editor no existe");
        }
        return dashboardRepository.getEditorMonthlyStats(editorId);
    }

    @Override
    public List<ChartPlotDto> getMonthlyPerformance(long editorId) {
        if (!userRepository.existsById(editorId)) {
            throw new BadCredentialsException("El editor no existe");
        }
        return dashboardRepository.getMonthlyPerformance(editorId);
    }

    @Override
    public List<ChartPlotDto> getTopMagazines(long editorId) {
        if (!userRepository.existsById(editorId)) {
            throw new BadCredentialsException("El editor no existe");
        }
        return dashboardRepository.getTopMagazines(editorId);
    }

    @Override
    public SubscriptionsResumeDto getSubscriptionsResume(long editorId) {
        if (!userRepository.existsById(editorId)) {
            throw new BadCredentialsException("El editor no existe");
        }
        return dashboardRepository.getSubscriptionsResume(editorId);
    }

    @Override
    public List<ExpiredAdBlockDto> getExpiredAdsBlock(long editorId) {
        if (!userRepository.existsById(editorId)) {
            throw new BadCredentialsException("El editor no existe");
        }
        return dashboardRepository.getExpiredAdsBlock(editorId);
    }
}
