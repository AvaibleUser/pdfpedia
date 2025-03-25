package org.cunoc.pdfpedia.controller.magazine;

import static org.springframework.http.HttpStatus.CREATED;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalTarjertDto;
import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.TopEditorDto;
import org.cunoc.pdfpedia.service.magazine.IMagazineService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/total-post")
    public ResponseEntity<TotalTarjertDto> totalPostAd(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){

        return ResponseEntity.status(HttpStatus.OK).body(this.magazineService.getTotalPostMagazine(startDate, endDate));
    }

    @GetMapping("/top-editor")
    public ResponseEntity<TopEditorDto> getTopEditorInRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(this.magazineService.getTopEditor(startDate, endDate));
    }

    @GetMapping("/count-by-month")
    public ResponseEntity<List<PostAdMount>> getAdCountsByMonth(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(this.magazineService.getMagazineCountsByMonth(startDate, endDate));
    }
}
