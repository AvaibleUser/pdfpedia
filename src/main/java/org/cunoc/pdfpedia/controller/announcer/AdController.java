package org.cunoc.pdfpedia.controller.announcer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.AdDto;
import org.cunoc.pdfpedia.domain.dto.announcer.AdPostDto;
import org.cunoc.pdfpedia.service.announcer.AdService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/announcers")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;


    @PostMapping("/{id}")
    public ResponseEntity<AdDto> createAd(@Valid @RequestBody AdPostDto adPostDto, @CurrentUserId long userId){
        AdDto createdAd = adService.create(adPostDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAd);
    }
}
