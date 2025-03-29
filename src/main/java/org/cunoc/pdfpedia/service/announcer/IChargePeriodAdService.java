package org.cunoc.pdfpedia.service.announcer;

import jakarta.validation.Valid;
import org.cunoc.pdfpedia.domain.dto.announcer.ChargePeriodAdDto;
import org.cunoc.pdfpedia.domain.dto.announcer.CountAdByTypeDto;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;

import java.util.List;

public interface IChargePeriodAdService {

    ChargePeriodAdDto toDto(ChargePeriodAdEntity chargePeriodAdEntity);

    List<ChargePeriodAdDto> findAll();

    ChargePeriodAdDto update(Long id, @Valid ChargePeriodAdDto dto);

    List<CountAdByTypeDto> getAdCountsByTypeForUser(Long advertiserId);
}
