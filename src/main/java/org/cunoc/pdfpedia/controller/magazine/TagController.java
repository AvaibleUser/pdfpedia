package org.cunoc.pdfpedia.controller.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.tag.TagDto;
import org.cunoc.pdfpedia.service.magazine.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<TagDto> getAllTags() {
        return tagService.findTags();
    }
}
