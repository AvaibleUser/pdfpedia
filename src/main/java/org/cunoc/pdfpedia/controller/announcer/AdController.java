package org.cunoc.pdfpedia.controller.announcer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.service.announcer.AdService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/announcers")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @PostMapping()
    public ResponseEntity<AdDto> createAd(@Valid @RequestBody AdPostDto adPostDto, @CurrentUserId long userId){
        AdDto createdAd = adService.create(adPostDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAd);
    }

    @GetMapping("/my-ads")
    public ResponseEntity<List<AdDto>> getMyAds(@CurrentUserId long userId){
        List<AdDto> list = this.adService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/my-ads-active")
    public ResponseEntity<List<AdDto>> getMyAdsActive(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @CurrentUserId long userId){
        List<AdDto> list = this.adService.findAllActiveByUserId(startDate, endDate, userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<AdDto> deactivateAd(@PathVariable long id){
        return ResponseEntity.status(HttpStatus.OK).body(this.adService.updateDeactivated(id));
    }

    @PutMapping("/activated/{id}")
    public ResponseEntity<AdDto> activated(@PathVariable long id, @Valid @RequestBody AdPostDto adPostDto, @CurrentUserId long userId){
        return ResponseEntity.status(HttpStatus.OK).body(this.adService.updateActive(id, adPostDto, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdDto> update(@PathVariable long id, @Valid @RequestBody AdUpdateDto adUpdateDto){
        return ResponseEntity.status(HttpStatus.OK).body(this.adService.update(id, adUpdateDto));
    }

    @GetMapping("/count-ads-userId")
    public ResponseEntity<TotalAdsDto> getTotalAdsByUserId(@CurrentUserId long userId){
        return ResponseEntity.status(HttpStatus.OK).body(this.adService.totalAdsByUserId(userId));
    }

    @GetMapping("post-count-mount")
    public ResponseEntity<List<PostAdMount>> getAllPostAdMount(@CurrentUserId long userId){
        return ResponseEntity.status(HttpStatus.OK).body(this.adService.getPostMount(userId));
    }

}
