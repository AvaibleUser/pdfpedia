package org.cunoc.pdfpedia.service.admin;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.MagazineAdminDto;
import org.cunoc.pdfpedia.domain.dto.admin.UpdateCostMagazineDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineCostTotalDto;
import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.dashboard.AnnouncersDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private final MagazineRepository magazineRepository;
    private final UserRepository userRepository;

    @Override
    public BigDecimal calculateTotalCost(Instant createdAt, BigDecimal costPerDay,LocalDate startDate, LocalDate endDate) {

        LocalDate createdDate = createdAt.atZone(ZoneId.systemDefault()).toLocalDate();;
        if (startDate != null && !createdDate.isAfter(startDate)) {
            createdDate = startDate;
        }
        LocalDate currentDate =  Objects.requireNonNullElseGet(endDate, () -> LocalDate.now(ZoneId.systemDefault()));


        // obtener los dias que han pasado fecha creacion y fecha actual
        long daysElapsed = ChronoUnit.DAYS.between(createdDate, currentDate);

        if (daysElapsed < 0) {
            return BigDecimal.ZERO;
        }

        return costPerDay.multiply(BigDecimal.valueOf(daysElapsed));
    }

    @Override
    public MagazineAdminDto toDto(MagazineEntity magazineEntity) {
        return MagazineAdminDto
                .builder()
                .id(magazineEntity.getId())
                .title(magazineEntity.getTitle())
                .costPerDay(magazineEntity.getCostPerDay())
                .createdAt(magazineEntity.getCreatedAt())
                .username(magazineEntity.getEditor().getUsername())
                .build();

    }

    @Override
    public MagazineCostTotalDto totalDto(MagazineEntity magazineEntity, LocalDate startDate, LocalDate endDate) {
        return MagazineCostTotalDto
                .builder()
                .costPerDay(magazineEntity.getCostPerDay())
                .title(magazineEntity.getTitle())
                .username(magazineEntity.getEditor().getUsername())
                .createdAt(magazineEntity.getCreatedAt())
                .costTotal(this.calculateTotalCost(magazineEntity.getCreatedAt(), magazineEntity.getCostPerDay(), startDate, endDate))
                .build();
    }

    @Override
    public boolean hasValidEditor(Long editorId) {
        return editorId != null && editorId > 0;
    }

    @Override
    public List<MagazineEntity> fetchMagazines(boolean costNull, Long editorId, boolean asc) {
        Sort sort = Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt");

        if (costNull && hasValidEditor(editorId)) {
            return magazineRepository.findAllByCostPerDayIsNullAndEditor_IdOrderByCreatedAt(editorId, sort);
        }
        if (costNull) {
            return magazineRepository.findAllByCostPerDayIsNullOrderByCreatedAt(sort);
        }
        if (hasValidEditor(editorId)) {
            return magazineRepository.findAllByEditor_IdOrderByCreatedAt(editorId, sort);
        }
        return magazineRepository.findAll(sort);
    }

    @Override
    public List<MagazineAdminDto> getAllMagazinesWithParams(boolean costNull, Long editorId, boolean asc) {
        List<MagazineEntity> magazines = fetchMagazines(costNull, editorId, asc);
        return magazines.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void updateCostMagazine(Long id, UpdateCostMagazineDto updateCostMagazineDto){
        MagazineEntity magazineEntity = magazineRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Magazine no encontrado para actalizar el costo"));

        magazineEntity.setCostPerDay(updateCostMagazineDto.costPerDay());

        this.magazineRepository.save(magazineEntity);
    }

    @Override
    public  List<AnnouncersDto> findAllEditors(){
        return this.userRepository.findAllByRole_Name("EDITOR", AnnouncersDto.class);
    }

    @Override
    @Transactional
    public List<MagazineCostTotalDto> getAllCostTotalMagazines(LocalDate startDate, LocalDate endDate){

        if (startDate == null || endDate == null) {
            return this.magazineRepository.findAllByCostPerDayIsNotNull()
                    .stream()
                    .map(magazine -> this.totalDto(magazine, null, null))
                    .toList();
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        return this.magazineRepository.findAllByCostPerDayIsNotNullAndCreatedAtLessThanEqual(endInstant)
                .stream()
                .map(magazine -> this.totalDto(magazine, startDate, endDate))
                .toList();
    }

    @Override
    @Transactional
    public List<PostAdMount> countRegisterByMonthByBetween(LocalDate startDate, LocalDate endDate){
        if (startDate == null || endDate== null){
            return this.userRepository.countRegisterByMonth();
        }
        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        return this.userRepository.countRegisterByMonthByBetween(startInstant,endInstant);

    }

}
