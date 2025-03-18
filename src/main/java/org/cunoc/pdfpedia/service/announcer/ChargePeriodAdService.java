package org.cunoc.pdfpedia.service.announcer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.ChargePeriodAdDto;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.announcer.ChargePeriodAdRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargePeriodAdService {

    private final ChargePeriodAdRepository chargePeriodAdRepository;

    private ChargePeriodAdDto toDto(ChargePeriodAdEntity chargePeriodAdEntity) {
        return new ChargePeriodAdDto(
                chargePeriodAdEntity.getId(),
                chargePeriodAdEntity.getAdType(),
                chargePeriodAdEntity.getDurationDays(),
                chargePeriodAdEntity.getCost()
        );
    }

    public List<ChargePeriodAdDto> findAll() {

        Iterable<ChargePeriodAdEntity> iterable =  this.chargePeriodAdRepository.findAll();

        return StreamSupport.stream(iterable.spliterator(), false)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ChargePeriodAdDto update(Long id, @Valid ChargePeriodAdDto dto) {
        ChargePeriodAdEntity existingEntity = chargePeriodAdRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Charge Period Ad not found"));

        existingEntity.setAdType(dto.adType());
        existingEntity.setDurationDays(dto.durationDays());
        existingEntity.setCost(dto.cost());

        ChargePeriodAdEntity updatedEntity = chargePeriodAdRepository.save(existingEntity);
        return this.toDto(updatedEntity);
    }

}
