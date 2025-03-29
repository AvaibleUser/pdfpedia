package org.cunoc.pdfpedia.service.admin;

import org.cunoc.pdfpedia.domain.dto.admin.MagazineAdminDto;
import org.cunoc.pdfpedia.domain.dto.admin.UpdateCostMagazineDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineCostTotalDto;
import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.dashboard.AnnouncersDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface IAdminService {

    BigDecimal calculateTotalCost(Instant createdAt, BigDecimal costPerDay, LocalDate startDate, LocalDate endDate);

    MagazineAdminDto toDto(MagazineEntity magazineEntity);

    MagazineCostTotalDto totalDto(MagazineEntity magazineEntity, LocalDate startDate, LocalDate endDate);

    boolean hasValidEditor(Long editorId);

    List<MagazineEntity> fetchMagazines(boolean costNull, Long editorId, boolean asc);

    List<MagazineAdminDto> getAllMagazinesWithParams(boolean costNull, Long editorId, boolean asc);

    void updateCostMagazine(Long id, UpdateCostMagazineDto updateCostMagazineDto);

    List<AnnouncersDto> findAllEditors();

    List<MagazineCostTotalDto> getAllCostTotalMagazines(LocalDate startDate, LocalDate endDate);

    List<PostAdMount> countRegisterByMonthByBetween(LocalDate startDate, LocalDate endDate);

}
