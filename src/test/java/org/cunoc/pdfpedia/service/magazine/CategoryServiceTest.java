package org.cunoc.pdfpedia.service.magazine;

import static org.mockito.BDDMockito.given;

import java.util.List;

import org.assertj.core.api.BDDAssertions;
import org.cunoc.pdfpedia.domain.dto.category.CategoryDto;
import org.cunoc.pdfpedia.repository.magazine.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void canFindCategories() {
        // given
        List<CategoryDto> expectedCategories = List.of(
            CategoryDto.builder()
                    .id(1L)
                    .name("amazing but not so amazing")
                    .description("no no no this is not so amazing")
                    .build(),
            CategoryDto.builder()
                    .id(2L)
                    .name("this time it is amazing")
                    .description("yeah this is amazing")
                    .build());

        given(categoryRepository.findAllCategoriesBy(CategoryDto.class)).willReturn(expectedCategories);

        // when
        List<CategoryDto> actualCategories = categoryService.findCategories();

        // then
        BDDAssertions.then(actualCategories)
                .usingRecursiveComparison()
                .isEqualTo(expectedCategories);
    }
}
