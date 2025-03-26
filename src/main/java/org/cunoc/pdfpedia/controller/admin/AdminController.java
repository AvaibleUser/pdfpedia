package org.cunoc.pdfpedia.controller.admin;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.MagazineAdminDto;
import org.cunoc.pdfpedia.domain.dto.admin.UpdateCostMagazineDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.AnnouncersDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.service.admin.AdminService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/magazines")
    public ResponseEntity<List<MagazineAdminDto>> getAllMagazinesWithParams(@RequestParam boolean costNull,
                                                                 @RequestParam Long editorId,
                                                                 @RequestParam boolean asc) {

        return ResponseEntity.ok(this.adminService.getAllMagazinesWithParams(costNull, editorId, asc));
    }

    @PutMapping("{id}")
    @ResponseStatus(OK)
    public void updateCostMagazine(@PathVariable Long id, @Valid @RequestBody UpdateCostMagazineDto updateCostMagazineDto) {
        this.adminService.updateCostMagazine(id, updateCostMagazineDto);
    }

    @GetMapping("/all-editors")
    public ResponseEntity<List<AnnouncersDto>> findAllAnnouncers(){
        List<AnnouncersDto> list = this.adminService.findAllEditors();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
