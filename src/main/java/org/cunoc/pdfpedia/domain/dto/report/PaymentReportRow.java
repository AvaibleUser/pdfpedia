package org.cunoc.pdfpedia.domain.dto.report;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.report.EditorReportData.ReportRow;

public record PaymentReportRow(
        Instant paidAt,
        String username,
        String magazineTitle,
        BigDecimal amount) implements ReportRow {

    public static final List<String> headers = List.of("Fecha del pago", "Usuario que hizo el pago", "Revista",
            "Monto del pago");

    @Override
    public List<Object> row() {
        return List.of(paidAt, username, magazineTitle, amount);
    }
}
