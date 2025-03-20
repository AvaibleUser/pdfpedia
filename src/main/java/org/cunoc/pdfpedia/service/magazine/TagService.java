package org.cunoc.pdfpedia.service.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.tag.TagDto;
import org.cunoc.pdfpedia.repository.magazine.TagRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService implements ITagService {

    private final TagRepository tagRepository;

    public List<TagDto> findTags() {
        return tagRepository.findAllTagsBy(TagDto.class);
    }
}
