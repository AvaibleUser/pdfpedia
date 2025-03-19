package org.cunoc.pdfpedia.controller.magazine;

import static org.mockito.BDDMockito.given;

import java.util.List;

import org.assertj.core.api.BDDAssertions;
import org.cunoc.pdfpedia.domain.dto.tag.TagDto;
import org.cunoc.pdfpedia.service.magazine.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TagControllerTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    @Test
    void canFindTags() {
        // given
        List<TagDto> expectedTags = List.of(
                TagDto.builder()
                        .id(1L)
                        .name("i'm atomic")
                        .build(),
                TagDto.builder()
                        .id(2L)
                        .name("noooo the reference")
                        .build());

        given(tagService.findTags()).willReturn(expectedTags);

        // when
        List<TagDto> actualTags = tagController.getAllTags();

        // then
        BDDAssertions.then(actualTags)
                .usingRecursiveComparison()
                .isEqualTo(expectedTags);
    }
}
