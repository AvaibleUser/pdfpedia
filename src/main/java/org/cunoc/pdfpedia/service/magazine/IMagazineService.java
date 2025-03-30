package org.cunoc.pdfpedia.service.magazine;

import java.time.LocalDate;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalTarjertDto;
import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineItemDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineEditorPreviewDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MinimalMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.TopEditorDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.cunoc.pdfpedia.domain.dto.magazine.UpdateMagazineDto;

public interface IMagazineService {

    MagazineDto findEditorMagazine(long editorId, long id);

    TotalTarjertDto getTotalPostMagazine(LocalDate startDate, LocalDate endDate);

    TopEditorDto getTopEditor(LocalDate startDate, LocalDate endDate);

    public List<PostAdMount> getMagazineCountsByMonth(LocalDate startDate, LocalDate endDate);

    Page<MagazineItemDto> getMagazinesByCategory(Long categoryId, Pageable pageable);

    MagazineItemDto getMagazineById(Long id);

    List<MinimalMagazineDto> findMinimalEditorMagazines(long editorId);

    List<MagazineEditorPreviewDto> findEditorMagazines(long editorId);

    MinimalMagazineDto saveMagazine(long editorId, AddMagazineDto magazine);

    MinimalMagazineDto updateMagazine(long editorId, long id, UpdateMagazineDto magazine);

    void deleteMagazine(long editorId, long id);

    List<MagazineItemDto> getUserMagazines(Long idUser);

    List<MagazineItemDto> getNewestMagazines();
}
