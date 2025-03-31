package org.cunoc.pdfpedia.controller.report;

import org.cunoc.pdfpedia.domain.dto.report.EditorReportFilterDto;
import org.cunoc.pdfpedia.domain.dto.report.EditorReportData;
import org.cunoc.pdfpedia.service.report.IEditorReportService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/editors/reports")
@RequiredArgsConstructor
public class EditorReportController {

    private final IEditorReportService editorReportService;

    @GetMapping(params = "type=comments")
    @RolesAllowed("EDITOR")
    public EditorReportData getCommentsReport(@CurrentUserId @Positive long editorId, @Valid EditorReportFilterDto filter) {
        return editorReportService.findCommentsReport(editorId, filter);
    }

    @GetMapping(params = "type=suscriptions")
    @RolesAllowed("EDITOR")
    public EditorReportData getSubscriptionsReport(@CurrentUserId @Positive long editorId,
            @Valid EditorReportFilterDto filter) {
        return editorReportService.findSubscriptionsReport(editorId, filter);
    }

    @GetMapping(params = "type=likes")
    @RolesAllowed("EDITOR")
    public EditorReportData getLikesReport(@CurrentUserId @Positive long editorId, @Valid EditorReportFilterDto filter) {
        return editorReportService.findLikesReport(editorId, filter);
    }

    @GetMapping(params = "type=payments")
    @RolesAllowed("EDITOR")
    public EditorReportData getPaymentsReport(@CurrentUserId @Positive long editorId, @Valid EditorReportFilterDto filter) {
        return editorReportService.findPaymentsReport(editorId, filter);
    }
}
