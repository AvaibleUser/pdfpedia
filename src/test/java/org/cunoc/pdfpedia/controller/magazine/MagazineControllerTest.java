package org.cunoc.pdfpedia.controller.magazine;

import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.service.magazine.MagazineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MagazineControllerTest {

    @Mock
    private MagazineService magazineService;

    @InjectMocks
    private MagazineController magazineController;

    @Test
    void canCreateMagazine() {
        // given
        long editorId = 501L;
        AddMagazineDto expectedMagazine = AddMagazineDto.builder().build();

        // when
        magazineController.createMagazine(editorId, expectedMagazine);

        // then
        BDDMockito.then(magazineService).should().saveMagazine(editorId, expectedMagazine);
    }
}
