package org.cunoc.pdfpedia.service.dashboard;

import static org.assertj.core.api.BDDAssertions.catchRuntimeException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.dashboard.ChartPlotDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.ExpiredAdBlockDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.StatDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.StatsDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.SubscriptionsResumeDto;
import org.cunoc.pdfpedia.repository.dashboard.DashboardRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
public class EditorDashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private EditorDashboardService editorDashboardService;

    @Test
    void canGetMonthlyStats() {
        // given
        long editorId = 501L;
        StatsDto expectedStats = StatsDto.builder()
                .wallet(new StatDto(100, 10))
                .likes(new StatDto(100, 10))
                .comments(new StatDto(100, 10))
                .subscriptions(new StatDto(100, 10))
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(dashboardRepository.getEditorMonthlyStats(editorId)).willReturn(expectedStats.toBuilder().build());

        // when
        StatsDto actualStats = editorDashboardService.getMonthlyStats(editorId);

        // then
        then(actualStats)
                .usingRecursiveComparison()
                .isEqualTo(expectedStats);
    }

    @Test
    void cantGetMonthlyStatsIfUserDoesntExist() {
        // given
        long editorId = 501L;

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorDashboardService.getMonthlyStats(editorId));

        // then
        then(actualException).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void canGetMonthlyPerformance() {
        // given
        long editorId = 501L;
        List<ChartPlotDto> performance = List.of(
                ChartPlotDto.builder()
                        .groupBy("2022-01-01")
                        .likes(100)
                        .comments(100)
                        .subscriptions(100)
                        .build(),
                ChartPlotDto.builder()
                        .groupBy("2022-02-01")
                        .likes(100)
                        .comments(100)
                        .subscriptions(100)
                        .build());
        List<ChartPlotDto> expectedPerformance = performance.stream()
                .map(plot -> plot.toBuilder().build())
                .toList();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(dashboardRepository.getMonthlyPerformance(editorId)).willReturn(performance);

        // when
        List<ChartPlotDto> actualPerformance = editorDashboardService.getMonthlyPerformance(editorId);

        // then
        then(actualPerformance)
                .usingRecursiveComparison()
                .isEqualTo(expectedPerformance);
    }

    @Test
    void cantGetMonthlyPerformanceIfUserDoesntExist() {
        // given
        long editorId = 501L;

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorDashboardService.getMonthlyPerformance(editorId));

        // then
        then(actualException).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void canGetTopMagazines() {
        // given
        long editorId = 501L;
        List<ChartPlotDto> topMagazines = List.of(
                ChartPlotDto.builder()
                        .groupBy("magazine 1")
                        .likes(100)
                        .comments(100)
                        .subscriptions(100)
                        .build(),
                ChartPlotDto.builder()
                        .groupBy("magazine 2")
                        .likes(100)
                        .comments(100)
                        .subscriptions(100)
                        .build());
        List<ChartPlotDto> expectedTopMagazines = topMagazines.stream()
                .map(plot -> plot.toBuilder().build())
                .toList();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(dashboardRepository.getTopMagazines(editorId)).willReturn(topMagazines);

        // when
        List<ChartPlotDto> actualTopMagazines = editorDashboardService.getTopMagazines(editorId);

        // then
        then(actualTopMagazines)
                .usingRecursiveComparison()
                .isEqualTo(expectedTopMagazines);
    }

    @Test
    void cantGetTopMagazinesIfUserDoesntExist() {
        // given
        long editorId = 501L;

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorDashboardService.getTopMagazines(editorId));

        // then
        then(actualException).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void canGetSubscriptionsResume() {
        // given
        long editorId = 501L;
        SubscriptionsResumeDto expectedSubscriptionsResume = new SubscriptionsResumeDto(100, 100);

        given(userRepository.existsById(editorId)).willReturn(true);
        given(dashboardRepository.getSubscriptionsResume(editorId)).willReturn(expectedSubscriptionsResume);

        // when
        SubscriptionsResumeDto actualSubscriptionsResume = editorDashboardService.getSubscriptionsResume(editorId);

        // then
        then(actualSubscriptionsResume)
                .usingRecursiveComparison()
                .isEqualTo(expectedSubscriptionsResume);
    }

    @Test
    void cantGetSubscriptionsResumeIfUserDoesntExist() {
        // given
        long editorId = 501L;

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorDashboardService.getSubscriptionsResume(editorId));

        // then
        then(actualException).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void canGetExpiredAdsBlock() {
        // given
        long editorId = 501L;
        List<ExpiredAdBlockDto> expiredAdsBlock = List.of(
                new ExpiredAdBlockDto(LocalDate.now().plusDays(10), "magazine 1"),
                new ExpiredAdBlockDto(LocalDate.now().plusDays(20), "magazine 2"));
        List<ExpiredAdBlockDto> expectedExpiredAdsBlock = expiredAdsBlock.stream()
                .map(ad -> ad.toBuilder().build())
                .toList();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(dashboardRepository.getExpiredAdsBlock(editorId)).willReturn(expiredAdsBlock);

        // when
        List<ExpiredAdBlockDto> actualExpiredAdsBlock = editorDashboardService.getExpiredAdsBlock(editorId);

        // then
        then(actualExpiredAdsBlock)
                .usingRecursiveComparison()
                .isEqualTo(expectedExpiredAdsBlock);
    }

    @Test
    void cantGetExpiredAdsBlockIfUserDoesntExist() {
        // given
        long editorId = 501L;

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorDashboardService.getExpiredAdsBlock(editorId));

        // then
        then(actualException).isInstanceOf(BadCredentialsException.class);
    }
}
