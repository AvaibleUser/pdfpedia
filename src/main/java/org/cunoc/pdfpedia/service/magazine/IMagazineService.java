package org.cunoc.pdfpedia.service.magazine;

import java.time.LocalDate;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalTarjertDto;
import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineItemDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazinePreviewDto;
import org.cunoc.pdfpedia.domain.dto.magazine.TopEditorDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMagazineService {

    List<MagazinePreviewDto> findEditorMagazines(long editorId);

    void saveMagazine(long editorId, AddMagazineDto magazine);

    TotalTarjertDto getTotalPostMagazine(LocalDate startDate, LocalDate endDate);

    TopEditorDto getTopEditor(LocalDate startDate, LocalDate endDate);

    public List<PostAdMount> getMagazineCountsByMonth(LocalDate startDate, LocalDate endDate);

    Page<MagazineItemDto> getMagazinesByCategory(Long categoryId, Pageable pageable);

    MagazineItemDto getMagazineById(Long id);
}
