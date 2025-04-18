package org.cunoc.pdfpedia.service.announcer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.ChargePeriodAdDto;
import org.cunoc.pdfpedia.domain.dto.announcer.CountAdByTypeDto;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.announcer.ChargePeriodAdRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargePeriodAdService implements IChargePeriodAdService {

    private final ChargePeriodAdRepository chargePeriodAdRepository;

    @Override
    public ChargePeriodAdDto toDto(ChargePeriodAdEntity chargePeriodAdEntity) {
        return new ChargePeriodAdDto(
                chargePeriodAdEntity.getId(),
                chargePeriodAdEntity.getAdType(),
                chargePeriodAdEntity.getDurationDays(),
                chargePeriodAdEntity.getCost()
        );
    }

    @Override
    public List<ChargePeriodAdDto> findAll() {

        Iterable<ChargePeriodAdEntity> iterable =  this.chargePeriodAdRepository.findAll();

        return StreamSupport.stream(iterable.spliterator(), false)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChargePeriodAdDto update(Long id, @Valid ChargePeriodAdDto dto) {
        ChargePeriodAdEntity existingEntity = chargePeriodAdRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Periodo de vigencia no encontrado"));

        existingEntity.setCost(dto.cost());

        ChargePeriodAdEntity updatedEntity = chargePeriodAdRepository.save(existingEntity);
        return this.toDto(updatedEntity);
    }

    @Override
    public List<CountAdByTypeDto> getAdCountsByTypeForUser(Long advertiserId) {
        return chargePeriodAdRepository.countAdsByTypeForUser(advertiserId);
    }

}
