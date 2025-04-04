package org.cunoc.pdfpedia.domain.dto.report;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.report.EditorReportData.ReportRow;

public record LikeReportRow(
        Object likeDate,
        String liker,
        String magazineTitle,
        Object magazinePublishDate,
        boolean disabledLikes) implements ReportRow {

    public static final List<String> headers = List.of(
            "Fecha del 'Me gusta'",
            "Quien dio 'Me gusta'",
            "Revista",
            "Fecha de publicación",
            "'Me gusta' desactivados");

    @Override
    public List<Object> row() {
        return List.of(likeDate instanceof Timestamp d ? d.toInstant() : likeDate instanceof Instant d ? d : likeDate,
                liker, magazineTitle,
                magazinePublishDate instanceof Timestamp d ? d.toInstant()
                        : magazinePublishDate instanceof Instant d ? d : magazinePublishDate,
                disabledLikes ? "Desactivado" : "Activo");
    }

    public static record TopLikedReportRow(
            long position,
            String magazineTitle,
            String author,
            long totalLikes,
            Object magazinePublishDate,
            String category) implements ReportRow {

        public static final List<String> headers = List.of(
                "Top",
                "Revista",
                "Autor",
                "Total de 'Me gusta'",
                "Fecha de publicación",
                "Categoría");

        @Override
        public List<Object> row() {
            return List.of(position, magazineTitle, author, totalLikes,
                    magazinePublishDate instanceof Timestamp d ? d.toInstant()
                            : magazinePublishDate instanceof Instant d ? d : magazinePublishDate,
                    category);
        }
    }
}
