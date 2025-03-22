package org.cunoc.pdfpedia.service.magazine;

import static org.mockito.BDDMockito.given;

import java.util.List;

import org.assertj.core.api.BDDAssertions;
import org.cunoc.pdfpedia.domain.dto.tag.TagDto;
import org.cunoc.pdfpedia.repository.magazine.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Test
    void canFindTags() {
        // given
        List<TagDto> expectedTags = List.of(
                TagDto.builder()
                        .id(1L)
                        .name("amazing")
                        .build(),
                TagDto.builder()
                        .id(2L)
                        .name("not so amazing")
                        .build());

        given(tagRepository.findAllTagsBy(TagDto.class)).willReturn(expectedTags);

        // when
        List<TagDto> actualTags = tagService.findTags();

        // then
        BDDAssertions.then(actualTags)
                .usingRecursiveComparison()
                .isEqualTo(expectedTags);
    }
}
