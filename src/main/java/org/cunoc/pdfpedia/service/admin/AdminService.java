package org.cunoc.pdfpedia.service.admin;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.MagazineAdminDto;
import org.cunoc.pdfpedia.domain.dto.admin.UpdateCostMagazineDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.AnnouncersDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MagazineRepository magazineRepository;
    private final UserRepository userRepository;

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

    private boolean hasValidEditor(Long editorId) {
        return editorId != null && editorId > 0;
    }

    private List<MagazineEntity> fetchMagazines(boolean costNull, Long editorId, boolean asc) {
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

    public List<MagazineAdminDto> getAllMagazinesWithParams(boolean costNull, Long editorId, boolean asc) {
        List<MagazineEntity> magazines = fetchMagazines(costNull, editorId, asc);
        return magazines.stream()
                .map(this::toDto)
                .toList();
    }

    public void updateCostMagazine(Long id, UpdateCostMagazineDto updateCostMagazineDto){
        MagazineEntity magazineEntity = magazineRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Magazine no encontrado para actalizar el costo"));

        magazineEntity.setCostPerDay(updateCostMagazineDto.costPerDay());

        this.magazineRepository.save(magazineEntity);
    }

    public  List<AnnouncersDto> findAllEditors(){
        return this.userRepository.findAllByRole_Name("EDITOR", AnnouncersDto.class);
    }


}
