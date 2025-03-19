package org.cunoc.pdfpedia.controller.magazine;

import static org.mockito.BDDMockito.given;

import java.util.List;

import org.assertj.core.api.BDDAssertions;
import org.cunoc.pdfpedia.domain.dto.category.CategoryDto;
import org.cunoc.pdfpedia.service.magazine.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void canFindCategories() {
        // given
        List<CategoryDto> expectedCategories = List.of(
                CategoryDto.builder()
                        .id(1L)
                        .name("amazing")
                        .description("amazing description")
                        .build(),
                CategoryDto.builder()
                        .id(2L)
                        .name("not so amazing")
                        .description("not so amazing description")
                        .build());

        given(categoryService.findCategories()).willReturn(expectedCategories);

        // when
        List<CategoryDto> actualCategories = categoryController.getAllCategories();

        // then
        BDDAssertions.then(actualCategories)
                .usingRecursiveComparison()
                .isEqualTo(expectedCategories);
    }
}
