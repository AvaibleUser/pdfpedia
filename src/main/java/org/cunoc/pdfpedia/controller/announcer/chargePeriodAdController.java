package org.cunoc.pdfpedia.controller.announcer;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.ChargePeriodAdDto;
import org.cunoc.pdfpedia.service.announcer.ChargePeriodAdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/announcers/charge-period-ads")
@RequiredArgsConstructor
public class chargePeriodAdController {

    private final ChargePeriodAdService chargePeriodAdService;

    @GetMapping
    public ResponseEntity<List<ChargePeriodAdDto>> getAll() {
        return ResponseEntity.ok(chargePeriodAdService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargePeriodAdDto> update(@PathVariable Long id, @Valid @RequestBody ChargePeriodAdDto dto) {
        return ResponseEntity.ok(chargePeriodAdService.update(id, dto));
    }


}
