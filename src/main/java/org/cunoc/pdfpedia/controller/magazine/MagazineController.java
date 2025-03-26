package org.cunoc.pdfpedia.controller.magazine;

import static org.springframework.http.HttpStatus.CREATED;

import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineItemDto;
import org.cunoc.pdfpedia.service.magazine.IMagazineService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public Page<MagazineItemDto> getMagazinesByCategory(@RequestParam Long idCategory, Pageable pageable) {
        return magazineService.getMagazinesByCategory(idCategory, pageable);
    }

    @GetMapping("/{id}")
    public MagazineItemDto getMagazine(@PathVariable Long id) {
        return magazineService.getMagazineById(id);
    }
}
