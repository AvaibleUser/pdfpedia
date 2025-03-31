package org.cunoc.pdfpedia.service.report;

import org.cunoc.pdfpedia.domain.dto.report.EditorReportFilterDto;
import org.cunoc.pdfpedia.domain.dto.report.EditorReportData;

public interface IEditorReportService {

    EditorReportData findCommentsReport(long editorId, EditorReportFilterDto filter);

    EditorReportData findSubscriptionsReport(long editorId, EditorReportFilterDto filter);

    EditorReportData findLikesReport(long editorId, EditorReportFilterDto filter);

    EditorReportData findPaymentsReport(long editorId, EditorReportFilterDto filter);
}
