package org.cunoc.pdfpedia.controller.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.category.CategoryDto;
import org.cunoc.pdfpedia.service.magazine.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.findCategories();
    }
}
