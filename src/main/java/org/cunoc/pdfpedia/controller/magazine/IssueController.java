package org.cunoc.pdfpedia.controller.magazine;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.issue.IssueTitleDto;
import org.cunoc.pdfpedia.domain.dto.issue.MagazineIssueDto;
import org.cunoc.pdfpedia.service.magazine.IIssueService;
import org.cunoc.pdfpedia.service.util.IStorageService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/magazines/{magazineId}/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IStorageService storageService;
    private final IIssueService issueService;

    @GetMapping
    public List<MagazineIssueDto> getMagazineIssues(@PathVariable @Positive long magazineId) {
        return issueService.findMagazineIssues(magazineId);
    }

    @PostMapping
    @RolesAllowed("EDITOR")
    @ResponseStatus(CREATED)
    public MagazineIssueDto createIssue(@CurrentUserId @Positive long editorId, @PathVariable @Positive long magazineId,
            @RequestPart("pdf") MultipartFile imageFile, @RequestBody IssueTitleDto issue) {
        String pdfUrl = storageService.uploadFile(imageFile);
        return issueService.saveIssue(editorId, magazineId, pdfUrl, issue);
    }

    @PutMapping("/{id}")
    @RolesAllowed("EDITOR")
    public MagazineIssueDto updateIssue(@CurrentUserId @Positive long editorId, @PathVariable @Positive long magazineId,
            @PathVariable @Positive long id, @RequestBody IssueTitleDto issue) {
        return issueService.updateIssue(editorId, magazineId, id, issue);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("EDITOR")
    public void deleteIssue(@CurrentUserId @Positive long editorId, @PathVariable @Positive long magazineId,
            @PathVariable @Positive long id) {
        issueService.deleteIssue(editorId, magazineId, id);
    }
}
