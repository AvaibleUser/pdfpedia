package org.cunoc.pdfpedia.service.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.category.CategoryDto;
import org.cunoc.pdfpedia.repository.magazine.CategoryRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> findCategories() {
        return categoryRepository.findAllCategoriesBy(CategoryDto.class);
    }
}
