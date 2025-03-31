package org.cunoc.pdfpedia.service.report;

import java.util.List;

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
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EditorReportService implements IEditorReportService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PaymentRepository paymentRepository;
    private final MagazineRepository magazineRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public EditorReportData findCommentsReport(long editorId, EditorReportFilterDto filter) {
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        if (filter.magazineId().isPresent()
                && !magazineRepository.existsByIdAndEditorId(filter.magazineId().get(), editorId)) {
            throw new BadRequestException("La revista no existe");
        }
        List<ReportRow> commentsReport = commentRepository.reportCommentsByMagazineIdAndBetween(
                editorId,
                filter.magazineId().orElse(null),
                filter.startDate().orElse(null),
                filter.endDate().orElse(null));

        ReportAggregateData aggregate = commentRepository.aggregateCommentsByMagazineIdAndBetween(
                editorId,
                filter.magazineId().orElse(null),
                filter.startDate().orElse(null),
                filter.endDate().orElse(null));

        return EditorReportData.builder()
                .headers(CommentReportRow.headers)
                .dataFrom(commentsReport)
                .stats(aggregate)
                .build();
    }

    @Override
    public EditorReportData findSubscriptionsReport(long editorId, EditorReportFilterDto filter) {
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        if (filter.magazineId().isPresent()
                && !magazineRepository.existsByIdAndEditorId(filter.magazineId().get(), editorId)) {
            throw new BadRequestException("La revista no existe");
        }
        List<ReportRow> subscriptionsReport = subscriptionRepository.reportSubscriptionsByMagazineIdAndBetween(
                editorId,
                filter.magazineId().orElse(null),
                filter.startDate().orElse(null),
                filter.endDate().orElse(null));

        ReportAggregateData aggregate = subscriptionRepository.aggregateSubscriptionsByMagazineIdAndBetween(
                editorId,
                filter.magazineId().orElse(null),
                filter.startDate().orElse(null),
                filter.endDate().orElse(null));

        return EditorReportData.builder()
                .headers(SubscriptionReportRow.headers)
                .dataFrom(subscriptionsReport)
                .stats(aggregate)
                .build();
    }

    @Override
    public EditorReportData findLikesReport(long editorId, EditorReportFilterDto filter) {
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        if (filter.magazineId().isPresent()
                && !magazineRepository.existsByIdAndEditorId(filter.magazineId().get(), editorId)) {
            throw new BadRequestException("La revista no existe");
        }
        List<LikeReportRow> likesReport = magazineRepository.reportLikesByEditorIdAndBetween(
                editorId,
                filter.magazineId().orElse(null),
                filter.startDate().orElse(null),
                filter.endDate().orElse(null));

        ReportAggregateData aggregate = magazineRepository.aggregateLikesByEditorIdAndBetween(
                editorId,
                filter.magazineId().orElse(null),
                filter.startDate().orElse(null),
                filter.endDate().orElse(null));

        List<TopLikedReportRow> topLikedReport = magazineRepository.reportTopLikedByEditorIdAndBetween(
                editorId,
                filter.magazineId().orElse(null),
                filter.startDate().orElse(null),
                filter.endDate().orElse(null),
                5);

        return EditorReportData.builder()
                .headers(LikeReportRow.headers)
                .dataFrom(likesReport)
                .stats(aggregate)
                .topDataHeaders(TopLikedReportRow.headers)
                .topDataFrom(topLikedReport)
                .build();
    }

    @Override
    public EditorReportData findPaymentsReport(long editorId, EditorReportFilterDto filter) {
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        if (filter.magazineId().isPresent()
                && !magazineRepository.existsByIdAndEditorId(filter.magazineId().get(), editorId)) {
            throw new BadRequestException("La revista no existe");
        }
        List<ReportRow> paymentsReport = paymentRepository.reportPaymentsByMagazineIdAndBetween(
                editorId,
                filter.magazineId().orElse(null),
                filter.startDate().orElse(null),
                filter.endDate().orElse(null));

        ReportAggregateData aggregate = paymentRepository.aggregatePaymentsByMagazineIdAndBetween(
                editorId,
                filter.magazineId().orElse(null),
                filter.startDate().orElse(null),
                filter.endDate().orElse(null));

        return EditorReportData.builder()
                .headers(PaymentReportRow.headers)
                .dataFrom(paymentsReport)
                .stats(aggregate)
                .build();
    }
}
