package org.cunoc.pdfpedia.service.magazine;

import java.util.List;
import java.util.Set;

import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazinePreviewDto;
import org.cunoc.pdfpedia.domain.entity.magazine.CategoryEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.TagEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.BadRequestException;
import org.cunoc.pdfpedia.repository.magazine.CategoryRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.magazine.TagRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MagazineService {

    private final UserRepository userRepository;
    private final MagazineRepository magazineRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public List<MagazinePreviewDto> findEditorMagazines(long editorId) {
        return magazineRepository.findAllByIsDeletedAndEditorId(false, editorId, MagazinePreviewDto.class);
    }

    public void saveMagazine(long editorId, AddMagazineDto magazine) {
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        if (!categoryRepository.existsById(magazine.categoryId())) {
            throw new BadRequestException("La categoría no existe");
        }
        if (!tagRepository.existsAllByIdIn(magazine.tagIds())) {
            throw new BadRequestException("Una o más etiquetas no existen");
        }
        UserEntity editor = userRepository.findById(editorId).get();
        CategoryEntity category = categoryRepository.findById(magazine.categoryId()).get();
        Set<TagEntity> tags = tagRepository.findAllByIdIn(magazine.tagIds());

        magazineRepository.save(MagazineEntity.builder()
                .title(magazine.title())
                .description(magazine.description())
                .adBlockingExpirationDate(magazine.adBlockingExpirationDate())
                .disableLikes(magazine.disableLikes())
                .disableComments(magazine.disableComments())
                .disableSuscriptions(magazine.disableSuscriptions())
                .category(category)
                .tags(tags)
                .editor(editor)
                .build());
    }
}
