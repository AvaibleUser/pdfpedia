package org.cunoc.pdfpedia.service.announcer;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.AdDto;
import org.cunoc.pdfpedia.domain.dto.announcer.AdPostDto;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.domain.utils.MapperAd;
import org.cunoc.pdfpedia.repository.announcer.AdRepository;
import org.cunoc.pdfpedia.repository.announcer.ChargePeriodAdRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final ChargePeriodAdRepository chargePeriodAdRepository;
    private final MapperAd mapperAd;

    public AdDto create(@Valid AdPostDto adPostDto, Long advertiserId) {
        UserEntity advertiser = userRepository.findById(advertiserId)
                .orElseThrow(() -> new ValueNotFoundException("usuarios no encontrado al realizar una publicacion de anuncio"));

        ChargePeriodAdEntity chargePeriodAd = chargePeriodAdRepository.findById(adPostDto.chargePeriodAd())
                .orElseThrow(() -> new ValueNotFoundException("Periodo de vigencia no valido al realizar una publicacion de anuncio"));

        AdEntity entity = mapperAd.toEntity(adPostDto, advertiser, chargePeriodAd);
        AdEntity savedEntity = adRepository.save(entity);

        return mapperAd.toDto(savedEntity);
    }
}
