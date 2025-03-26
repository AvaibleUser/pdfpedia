package org.cunoc.pdfpedia.service.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.issue.IssueTitleDto;
import org.cunoc.pdfpedia.domain.dto.issue.MagazineIssueDto;

public interface IIssueService {

    MagazineIssueDto findMagazineIssue(long magazineId, long issueId);

    List<MagazineIssueDto> findMagazineIssues(long magazineId);

    MagazineIssueDto saveIssue(long editorId, long magazineId, String pdfUrl, IssueTitleDto issue);

    MagazineIssueDto updateIssue(long editorId, long magazineId, long issueId, IssueTitleDto issue);

    void deleteIssue(long editorId, long magazineId, long issueId);
}
