package org.cunoc.pdfpedia.service.magazine;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalTarjertDto;
import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineItemDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineEditorPreviewDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MinimalMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.TopEditorDto;
import org.cunoc.pdfpedia.domain.dto.magazine.UpdateMagazineDto;
import org.cunoc.pdfpedia.domain.entity.magazine.CategoryEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.TagEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.BadRequestException;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.magazine.CategoryRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.magazine.TagRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MagazineService implements IMagazineService {

    private final UserRepository userRepository;
    private final MagazineRepository magazineRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Override
    public MagazineDto findEditorMagazine(long editorId, long id) {
        return magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(id, editorId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontró la revista"));
    }

    @Override
    public List<MinimalMagazineDto> findMinimalEditorMagazines(long editorId) {
        return magazineRepository.findAllSimpleByEditorIdAndIsDeletedFalseOrderById(editorId, MinimalMagazineDto.class);
    }

    @Override
    @Transactional
    public List<MagazineEditorPreviewDto> findEditorMagazines(long editorId) {
        return magazineRepository.findAllByEditorIdAndIsDeletedFalse(editorId);
    }

    @Override
    @Transactional
    public MinimalMagazineDto saveMagazine(long editorId, AddMagazineDto magazine) {
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

        MagazineEntity dbMagazine = magazineRepository.save(MagazineEntity.builder()
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

        return MinimalMagazineDto.builder()
                .id(dbMagazine.getId())
                .title(dbMagazine.getTitle())
                .build();
    }

    @Override
    @Transactional
    public MinimalMagazineDto updateMagazine(long editorId, long magazineId, UpdateMagazineDto newMagazine) {
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        if (!magazineRepository.existsById(magazineId)) {
            throw new BadRequestException("La revista no existe");
        }
        MagazineEntity magazine = magazineRepository
                .findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontró la revista"));

        newMagazine.title().filter(StringUtils::isNotBlank).ifPresent(magazine::setTitle);
        newMagazine.description().filter(StringUtils::isNotBlank).ifPresent(magazine::setDescription);
        newMagazine.disableLikes().ifPresent(magazine::setDisableLikes);
        newMagazine.disableComments().ifPresent(magazine::setDisableComments);
        newMagazine.disableSuscriptions().ifPresent(magazine::setDisableSuscriptions);
        newMagazine.categoryId().flatMap(categoryRepository::findById).ifPresent(magazine::setCategory);
        newMagazine.tagIds()
                .filter(ObjectUtils::isNotEmpty)
                .map(tagRepository::findAllByIdIn)
                .ifPresent(magazine::setTags);

        if (newMagazine.adBlockingExpirationDate().isPresent()) {
            magazine.setAdBlockingExpirationDate(newMagazine.adBlockingExpirationDate().get());
        } else if (magazine.getAdBlockingExpirationDate() != null) {
            magazine.setAdBlockingExpirationDate(null);
        }

        magazine = magazineRepository.save(magazine);

        return MinimalMagazineDto.builder()
                .id(magazine.getId())
                .title(magazine.getTitle())
                .build();
    }

    @Override
    public void deleteMagazine(long editorId, long magazineId) {
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class)
                .ifPresent(m -> {
                    m.setDeleted(true);
                    magazineRepository.save(m);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MagazineItemDto> getMagazinesByCategory(Long categoryId, Pageable pageable) {
        return magazineRepository.findByCategoryId(categoryId, pageable)
                .map(MagazineItemDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public MagazineItemDto getMagazineById(Long id) {

        if (!magazineRepository.existsById(id)) {
            throw new BadRequestException("Magazine not found");
        }

        return MagazineItemDto.fromEntity(magazineRepository.findById(id).get());
    }

    public TotalTarjertDto getTotalPostMagazine(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return TotalTarjertDto
                    .builder()
                    .total(this.magazineRepository.count())
                    .build();
        }
        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        return TotalTarjertDto
                .builder()
                .total(this.magazineRepository.countAllByCreatedAtBetween(startInstant, endInstant))
                .build();
    }

    public TopEditorDto getTopEditor(LocalDate startDate, LocalDate endDate) {

        if (startDate == null && endDate == null) {

            UserEntity editor = magazineRepository
                    .findAllByIsDeletedFalseOrderByEditor(PageRequest.of(0, 1))
                    .stream()
                    .map(MagazineEntity::getEditor)
                    .findFirst().orElseThrow(() -> new ValueNotFoundException("No hay registros"));

            return TopEditorDto
                    .builder()
                    .userName(editor.getUsername())
                    .build();
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        UserEntity editor = magazineRepository
                .findAllByIsDeletedFalseAndCreatedAtBetweenOrderByEditor(startInstant, endInstant, PageRequest.of(0, 1))
                .stream()
                .map(MagazineEntity::getEditor)
                .findFirst().orElseThrow(() -> new ValueNotFoundException("No hay registros"));

        return TopEditorDto
                .builder()
                .userName(editor.getUsername())
                .build();


    }

    public List<PostAdMount> getMagazineCountsByMonth(LocalDate startDate, LocalDate endDate) {

        if (startDate == null && endDate == null) {
            return this.magazineRepository.countMagazineByMonth();
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        return magazineRepository.countMagazineByMonthByBetween(startInstant, endInstant);
    }



}
