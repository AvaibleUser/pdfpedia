package org.cunoc.pdfpedia.service.magazine;

import static org.assertj.core.api.BDDAssertions.catchRuntimeException;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.BDDAssertions;
import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.entity.magazine.CategoryEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.TagEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.BadRequestException;
import org.cunoc.pdfpedia.repository.magazine.CategoryRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.magazine.TagRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MagazineServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MagazineRepository magazineRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private MagazineService magazineService;

    @Test
    void canSaveMagazine() {
        // given
        long editorId = 501L;
        long categoryId = 150L;
        List<Long> tagIds = List.of(10L, 40L);

        UserEntity editor = mock(UserEntity.class);
        CategoryEntity category = mock(CategoryEntity.class);
        Set<TagEntity> tags = Set.of(mock(TagEntity.class), mock(TagEntity.class));

        MagazineEntity expectedMagazine = MagazineEntity.builder()
                .title("title")
                .description("description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .category(category)
                .tags(tags)
                .editor(editor)
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(categoryRepository.existsById(categoryId)).willReturn(true);
        given(tagRepository.existsAllByIdIn(tagIds)).willReturn(true);

        given(userRepository.findById(editorId)).willReturn(Optional.of(editor));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(tagRepository.findAllByIdIn(tagIds)).willReturn(tags);

        given(magazineRepository.save(refEq(expectedMagazine))).willReturn(expectedMagazine);

        AddMagazineDto inMagazine = AddMagazineDto.builder()
                .title("title")
                .description("description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .categoryId(categoryId)
                .tagIds(tagIds)
                .build();

        // when
        magazineService.saveMagazine(editorId, inMagazine);

        // then
        then(userRepository).should().existsById(editorId);
        then(categoryRepository).should().existsById(categoryId);
        then(tagRepository).should().existsAllByIdIn(refEq(tagIds));
        then(magazineRepository).should().save(refEq(expectedMagazine));
    }

    @Test
    void cantSaveMagazineIfUserDoesntExist() {
        // given
        long editorId = 501L;
        long categoryId = 150L;
        List<Long> tagIds = List.of(10L, 40L);

        given(userRepository.existsById(editorId)).willReturn(false);

        AddMagazineDto inMagazine = AddMagazineDto.builder()
                .title("title")
                .description("description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .categoryId(categoryId)
                .tagIds(tagIds)
                .build();

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.saveMagazine(editorId, inMagazine));

        // then
        then(userRepository).should().existsById(editorId);
        BDDAssertions.then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantSaveMagazineIfCategoryDoesntExist() {
        // given
        long editorId = 501L;
        long categoryId = 150L;
        List<Long> tagIds = List.of(10L, 40L);

        given(userRepository.existsById(editorId)).willReturn(true);
        given(categoryRepository.existsById(categoryId)).willReturn(false);

        AddMagazineDto inMagazine = AddMagazineDto.builder()
                .title("title")
                .description("description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .categoryId(categoryId)
                .tagIds(tagIds)
                .build();

        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.saveMagazine(editorId, inMagazine));

        // then
        then(categoryRepository).should().existsById(categoryId);
        BDDAssertions.then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantSaveMagazineIfTagsDoesntExist() {
        // given
        long editorId = 501L;
        long categoryId = 150L;
        List<Long> tagIds = List.of(10L, 40L);

        given(userRepository.existsById(editorId)).willReturn(true);
        given(categoryRepository.existsById(categoryId)).willReturn(true);
        given(tagRepository.existsAllByIdIn(tagIds)).willReturn(false);

        AddMagazineDto inMagazine = AddMagazineDto.builder()
                .title("title")
                .description("description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .categoryId(categoryId)
                .tagIds(tagIds)
                .build();

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.saveMagazine(editorId, inMagazine));

        // then
        then(tagRepository).should().existsAllByIdIn(refEq(tagIds));
        BDDAssertions.then(actualException).isInstanceOf(BadRequestException.class);
    }
}
