package org.cunoc.pdfpedia.controller.magazine;

import static org.springframework.http.HttpStatus.CREATED;

import java.time.LocalDate;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalTarjertDto;
import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MinimalMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.TopEditorDto;
import org.cunoc.pdfpedia.domain.dto.magazine.UpdateMagazineDto;
import org.cunoc.pdfpedia.service.magazine.IMagazineService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/magazines")
@RequiredArgsConstructor
public class MagazineController {

    private final IMagazineService magazineService;

    @GetMapping(params = "type=published")
    @RolesAllowed("EDITOR")
    public List<?> findEditorMagazines(@CurrentUserId @Positive long editorId,
            @RequestParam(defaultValue = "true") boolean complete) {
        if (complete) {
            return magazineService.findEditorMagazines(editorId);
        }
        return magazineService.findMinimalEditorMagazines(editorId);
    }

    @GetMapping(path = "/{id}", params = "type=published")
    @RolesAllowed("EDITOR")
    public MagazineDto findEditorMagazine(@CurrentUserId @Positive long editorId, @PathVariable @Positive long id) {
        return magazineService.findEditorMagazine(editorId, id);
    }

    @PostMapping
    @RolesAllowed("EDITOR")
    @ResponseStatus(CREATED)
    public MinimalMagazineDto createMagazine(@CurrentUserId @Positive long editorId,
            @RequestBody @Valid AddMagazineDto magazine) {
        return magazineService.saveMagazine(editorId, magazine);
    }

    @PutMapping("/{id}")
    @RolesAllowed("EDITOR")
    @ResponseStatus(CREATED)
    public MinimalMagazineDto updateMagazine(@CurrentUserId @Positive long editorId, @PathVariable @Positive long id,
            @RequestBody @Valid UpdateMagazineDto magazine) {
        return magazineService.updateMagazine(editorId, id, magazine);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("EDITOR")
    public void deleteMagazine(@CurrentUserId @Positive long editorId, @PathVariable long id) {
        magazineService.deleteMagazine(editorId, id);
    }

    @GetMapping("/total-post")
    public ResponseEntity<TotalTarjertDto> totalPostAd(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.status(HttpStatus.OK).body(this.magazineService.getTotalPostMagazine(startDate, endDate));
    }

    @GetMapping("/top-editor")
    public ResponseEntity<TopEditorDto> getTopEditorInRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.status(HttpStatus.OK).body(this.magazineService.getTopEditor(startDate, endDate));
    }

    @GetMapping("/count-by-month")
    public ResponseEntity<List<PostAdMount>> getAdCountsByMonth(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(this.magazineService.getMagazineCountsByMonth(startDate, endDate));
    }
}
