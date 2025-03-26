package org.cunoc.pdfpedia.service.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineItemDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazinePreviewDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMagazineService {

    List<MagazinePreviewDto> findEditorMagazines(long editorId);

    void saveMagazine(long editorId, AddMagazineDto magazine);

    Page<MagazineItemDto> getMagazinesByCategory(Long categoryId, Pageable pageable);

    MagazineItemDto getMagazineById(Long id);
}
