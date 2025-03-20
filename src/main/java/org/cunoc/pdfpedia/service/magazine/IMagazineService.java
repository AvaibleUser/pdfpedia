package org.cunoc.pdfpedia.service.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazinePreviewDto;

public interface IMagazineService {

    List<MagazinePreviewDto> findEditorMagazines(long editorId);

    void saveMagazine(long editorId, AddMagazineDto magazine);
}
