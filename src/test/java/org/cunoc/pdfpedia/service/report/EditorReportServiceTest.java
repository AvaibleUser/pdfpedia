package org.cunoc.pdfpedia.service.report;

import static org.assertj.core.api.BDDAssertions.catchRuntimeException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.dto.report.CommentReportRow;
import org.cunoc.pdfpedia.domain.dto.report.EditorReportData;
import org.cunoc.pdfpedia.domain.dto.report.EditorReportData.ReportRow;
import org.cunoc.pdfpedia.domain.dto.report.EditorReportFilterDto;
import org.cunoc.pdfpedia.domain.dto.report.LikeReportRow;
import org.cunoc.pdfpedia.domain.dto.report.LikeReportRow.TopLikedReportRow;
import org.cunoc.pdfpedia.domain.dto.report.PaymentReportRow;
import org.cunoc.pdfpedia.domain.dto.report.ReportAggregateData;
import org.cunoc.pdfpedia.domain.dto.report.SubscriptionReportRow;
import org.cunoc.pdfpedia.domain.exception.BadRequestException;
import org.cunoc.pdfpedia.repository.interaction.CommentRepository;
import org.cunoc.pdfpedia.repository.interaction.SubscriptionRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.monetary.PaymentRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EditorReportServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MagazineRepository magazineRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private EditorReportService editorReportService;

    @Test
    void canFindCommentsReport() {
        // given
        long editorId = 501L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.empty())
                .build();

        List<ReportRow> rows = List.of(mock(CommentReportRow.class));
        ReportAggregateData aggregate = mock(ReportAggregateData.class);

        EditorReportData expectedReport = EditorReportData.builder()
                .headers(CommentReportRow.headers)
                .dataFrom(rows)
                .stats(aggregate)
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(commentRepository.reportCommentsByMagazineIdAndBetween(editorId, null, null, null))
                .willReturn(rows);
        given(commentRepository.aggregateCommentsByMagazineIdAndBetween(editorId, null, null, null))
                .willReturn(aggregate);

        // when
        EditorReportData actualReport = editorReportService.findCommentsReport(editorId, filter);

        // then
        then(actualReport)
                .usingRecursiveComparison()
                .isEqualTo(expectedReport);
    }

    @Test
    void cantFindCommentsReportIfUserDoesntExist() {
        // given
        long editorId = 501L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.empty())
                .build();

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorReportService.findCommentsReport(editorId, filter));

        // then
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantFindCommentsReportIfEditorMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 100L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.of(magazineId))
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsByIdAndEditorId(magazineId, editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorReportService.findCommentsReport(editorId, filter));

        // then
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void canFindSubscriptionsReport() {
        // given
        long editorId = 501L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.empty())
                .build();

        List<ReportRow> rows = List.of(mock(SubscriptionReportRow.class));
        ReportAggregateData aggregate = mock(ReportAggregateData.class);
        EditorReportData expectedReport = EditorReportData.builder()
                .headers(SubscriptionReportRow.headers)
                .dataFrom(rows)
                .stats(aggregate)
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(subscriptionRepository.reportSubscriptionsByMagazineIdAndBetween(editorId, null, null, null))
                .willReturn(rows);
        given(subscriptionRepository.aggregateSubscriptionsByMagazineIdAndBetween(editorId, null, null, null))
                .willReturn(aggregate);

        // when
        EditorReportData actualReport = editorReportService.findSubscriptionsReport(editorId, filter);

        // then
        then(actualReport)
                .usingRecursiveComparison()
                .isEqualTo(expectedReport);
    }

    @Test
    void cantFindSubscriptionsReportIfUserDoesntExist() {
        // given
        long editorId = 501L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.empty())
                .build();

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorReportService.findSubscriptionsReport(editorId, filter));

        // then
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantFindSubscriptionsReportIfEditorMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 100L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.of(magazineId))
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsByIdAndEditorId(magazineId, editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorReportService.findSubscriptionsReport(editorId, filter));

        // then
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void canFindLikesReport() {
        // given
        long editorId = 501L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.empty())
                .build();

        List<LikeReportRow> rows = List.of(mock(LikeReportRow.class));
        ReportAggregateData aggregate = mock(ReportAggregateData.class);
        List<TopLikedReportRow> topLikedRows = List.of(mock(TopLikedReportRow.class));
        EditorReportData expectedReport = EditorReportData.builder()
                .headers(LikeReportRow.headers)
                .dataFrom(rows)
                .stats(aggregate)
                .topDataHeaders(TopLikedReportRow.headers)
                .topDataFrom(topLikedRows)
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.reportLikesByEditorIdAndBetween(editorId, null, null, null))
                .willReturn(rows);
        given(magazineRepository.aggregateLikesByEditorIdAndBetween(editorId, null, null, null))
                .willReturn(aggregate);
        given(magazineRepository.reportTopLikedByEditorIdAndBetween(editorId, null, null, null, 5))
                .willReturn(topLikedRows);

        // when
        EditorReportData actualReport = editorReportService.findLikesReport(editorId, filter);

        // then
        then(actualReport)
                .usingRecursiveComparison()
                .isEqualTo(expectedReport);
    }

    @Test
    void cantFindLikesReportIfUserDoesntExist() {
        // given
        long editorId = 501L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.empty())
                .build();

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorReportService.findLikesReport(editorId, filter));

        // then
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantFindLikesReportIfEditorMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 100L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.of(magazineId))
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsByIdAndEditorId(magazineId, editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorReportService.findLikesReport(editorId, filter));

        // then
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void canFindPaymentsReport() {
        // given
        long editorId = 501L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.empty())
                .build();

        List<ReportRow> rows = List.of(mock(PaymentReportRow.class));
        ReportAggregateData aggregate = mock(ReportAggregateData.class);
        EditorReportData expectedReport = EditorReportData.builder()
                .headers(PaymentReportRow.headers)
                .dataFrom(rows)
                .stats(aggregate)
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(paymentRepository.reportPaymentsByMagazineIdAndBetween(editorId, null, null, null))
                .willReturn(rows);
        given(paymentRepository.aggregatePaymentsByMagazineIdAndBetween(editorId, null, null, null))
                .willReturn(aggregate);

        // when
        EditorReportData actualReport = editorReportService.findPaymentsReport(editorId, filter);

        // then
        then(actualReport)
                .usingRecursiveComparison()
                .isEqualTo(expectedReport);
    }

    @Test
    void cantFindPaymentsReportIfUserDoesntExist() {
        // given
        long editorId = 501L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.empty())
                .build();

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorReportService.findPaymentsReport(editorId, filter));

        // then
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantFindPaymentsReportIfEditorMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 100L;
        EditorReportFilterDto filter = EditorReportFilterDto.builder()
                .startDate(Optional.empty())
                .endDate(Optional.empty())
                .magazineId(Optional.of(magazineId))
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsByIdAndEditorId(magazineId, editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> editorReportService.findPaymentsReport(editorId, filter));

        // then
        then(actualException).isInstanceOf(BadRequestException.class);
    }
}
