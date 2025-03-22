package org.cunoc.pdfpedia.controller.magazine;

import static org.springframework.http.HttpStatus.CREATED;

import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.service.magazine.IMagazineService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/magazines")
@RequiredArgsConstructor
public class MagazineController {

    private final IMagazineService magazineService;

    @PostMapping
    @RolesAllowed("EDITOR")
    @ResponseStatus(CREATED)
    public void createMagazine(@CurrentUserId long editorId, @RequestBody @Valid AddMagazineDto magazine) {
        magazineService.saveMagazine(editorId, magazine);
    }
}
