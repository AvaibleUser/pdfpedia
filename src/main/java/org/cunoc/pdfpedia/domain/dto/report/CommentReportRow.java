package org.cunoc.pdfpedia.domain.dto.report;

import java.time.Instant;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.report.EditorReportData.ReportRow;

public record CommentReportRow(
        Instant commentDate,
        String commenter,
        String commentContent,
        String magazineTitle,
        Instant magazinePublishDate,
        boolean disabledComments) implements ReportRow {

    public static final List<String> headers = List.of(
            "Fecha del comentario",
            "Comentador",
            "Comentario",
            "Revista",
            "Fecha de publicación",
            "Comentarios desactivados");

    @Override
    public List<Object> row() {
        return List.of(commentDate, commenter, commentContent, magazineTitle, magazinePublishDate,
                disabledComments ? "Desactivado" : "Activo");
    }
}