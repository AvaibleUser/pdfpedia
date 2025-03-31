package org.cunoc.pdfpedia.domain.dto.report;

import java.time.Instant;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.report.EditorReportData.ReportRow;

public record SubscriptionReportRow(
        String suscriptor,
        Instant subscribedAt,
        boolean deletedSubscription,
        String magazineTitle,
        Instant magazinePublishDate,
        boolean disabledSuscriptions) implements ReportRow {

    public static final List<String> headers = List.of(
            "Suscriptor",
            "Fecha de suscripción",
            "Estado de suscripción",
            "Revista",
            "Fecha de publicación",
            "Suscripciones desactivadas");

    @Override
    public List<Object> row() {
        return List.of(suscriptor, subscribedAt, deletedSubscription ? "Inactiva" : "Activa", magazineTitle,
                magazinePublishDate, disabledSuscriptions ? "Desactivado" : "Activo");
    }
}