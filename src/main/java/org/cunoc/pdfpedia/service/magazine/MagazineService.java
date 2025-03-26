package org.cunoc.pdfpedia.service.magazine;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalTarjertDto;
import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineItemDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazinePreviewDto;
import org.cunoc.pdfpedia.domain.dto.magazine.TopEditorDto;
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
        if (startDate == null && endDate == null){
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

        if (startDate == null && endDate == null){

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

        if (startDate == null && endDate == null){
            return this.magazineRepository.countMagazineByMonth();
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        return magazineRepository.countMagazineByMonthByBetween(startInstant, endInstant);
    }



}
