package org.cunoc.pdfpedia.service;


import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.configuration.ConfigurationDto;
import org.cunoc.pdfpedia.domain.dto.configuration.UpdateCostHidingAdDayDto;
import org.cunoc.pdfpedia.domain.dto.configuration.UpdateCostMagazineDayDto;
import org.cunoc.pdfpedia.domain.entity.ConfigurationEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public ConfigurationDto getConfiguration() {
        return this.configurationRepository.findFirstByOrderByIdAsc(ConfigurationDto.class)
                .orElseThrow(()-> new ValueNotFoundException("No existen configuraciones"));
    }

    public void updateCostHidingAdDayConfiguration(long id, UpdateCostHidingAdDayDto updateCostHidingAdDayDto) {
        ConfigurationEntity exist  = this.configurationRepository.findById(id)
                .orElseThrow(()-> new ValueNotFoundException("Configurcion a editar es invalida"));

        exist.setCostHidingAdDay(updateCostHidingAdDayDto.costHidingAdDay());
        this.configurationRepository.save(exist);
    }

    public void updateCostMagazineDayConfiguration(long id, UpdateCostMagazineDayDto updateCostMagazineDayDto) {
        ConfigurationEntity exist  = this.configurationRepository.findById(id)
                .orElseThrow(()-> new ValueNotFoundException("Configurcion a editar es invalida"));

        exist.setCostMagazineDay(updateCostMagazineDayDto.costMagazineDay());
        this.configurationRepository.save(exist);
    }


}
