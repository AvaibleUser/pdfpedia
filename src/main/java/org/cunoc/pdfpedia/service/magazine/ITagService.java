package org.cunoc.pdfpedia.service.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.tag.TagDto;

public interface ITagService {

    List<TagDto> findTags();
}
