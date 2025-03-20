package org.cunoc.pdfpedia.service.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.category.CategoryDto;

public interface ICategoryService {

    List<CategoryDto> findCategories();
}
