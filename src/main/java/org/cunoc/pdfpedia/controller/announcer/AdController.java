package org.cunoc.pdfpedia.controller.announcer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.AdDto;
import org.cunoc.pdfpedia.domain.dto.announcer.AdPostDto;
import org.cunoc.pdfpedia.domain.dto.announcer.AdUpdateDto;
import org.cunoc.pdfpedia.service.announcer.AdService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
