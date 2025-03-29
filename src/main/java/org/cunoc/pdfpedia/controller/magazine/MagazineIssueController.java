package org.cunoc.pdfpedia.controller.magazine;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineIssueDto;
import org.cunoc.pdfpedia.service.magazine.MagazineIssueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/v1/magazines/{id}/issues")
@RequiredArgsConstructor
public class MagazineIssueController {

    private final MagazineIssueService getMagazineIssues;

    @GetMapping()
    public Set<MagazineIssueDto> getMagazineIssues(@PathVariable Long id) {
        return getMagazineIssues.getMagazineIssues(id);
    }
}
