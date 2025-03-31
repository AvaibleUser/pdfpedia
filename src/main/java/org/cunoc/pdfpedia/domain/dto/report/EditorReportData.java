package org.cunoc.pdfpedia.domain.dto.report;

import java.util.Collection;
import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record EditorReportData(
        List<String> headers,
        List<List<Object>> data,
        ReportAggregateData stats,
        List<String> topDataHeaders,
        List<List<Object>> topData) {

    public static interface ReportRow {
        List<Object> row();
    }

    public static class EditorReportDataBuilder {
        public EditorReportDataBuilder dataFrom(Collection<? extends ReportRow> data) {
            this.data = data.stream().map(ReportRow::row).toList();
            return this;
        }

        public EditorReportDataBuilder topDataFrom(Collection<? extends ReportRow> topData) {
            this.topData = topData.stream().map(ReportRow::row).toList();
            return this;
        }
    }
}
