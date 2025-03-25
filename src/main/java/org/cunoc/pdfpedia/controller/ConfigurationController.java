package org.cunoc.pdfpedia.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.configuration.ConfigurationDto;
import org.cunoc.pdfpedia.domain.dto.configuration.UpdateCostHidingAdDayDto;
import org.cunoc.pdfpedia.domain.dto.configuration.UpdateCostMagazineDayDto;
import org.cunoc.pdfpedia.service.ConfigurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/configurations")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<ConfigurationDto> getConfiguration(){
        return ResponseEntity.ok(configurationService.getConfiguration());
    }

    @ResponseStatus(CREATED)
    @PutMapping("/cost-hiding/{id}")
    public void updateCostHidingAdDayConfiguration(@Valid @RequestBody UpdateCostHidingAdDayDto updateCostHidingAdDayDto, @PathVariable long id){
        this.configurationService.updateCostHidingAdDayConfiguration(id,updateCostHidingAdDayDto);
    }

    @ResponseStatus(CREATED)
    @PutMapping("/cost-magazine/{id}")
    public void updateCostMagazineDayConfiguration(@Valid @RequestBody UpdateCostMagazineDayDto updateCostMagazineDayDto, @PathVariable long id){
        this.configurationService.updateCostMagazineDayConfiguration(id,updateCostMagazineDayDto);
    }
}
