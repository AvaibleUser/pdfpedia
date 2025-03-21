package org.cunoc.pdfpedia.controller.announcer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.AdViewCreateDto;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalViewsAdDto;
import org.cunoc.pdfpedia.service.announcer.AdViewsService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/announcers/views")
@RequiredArgsConstructor
public class AdViewsController {

    private final AdViewsService adViewsService;

    @PostMapping
    @ResponseStatus(CREATED)
    public void addAdViews(@Valid @RequestBody AdViewCreateDto adViewCreateDto) {
        this.adViewsService.create(adViewCreateDto);
    }

    @GetMapping("/total")
    public ResponseEntity<TotalViewsAdDto> getTotal(@CurrentUserId long userId) {
       return ResponseEntity.status(HttpStatus.OK).body(this.adViewsService.getTotalViews(userId));
    }

}
