package org.cunoc.pdfpedia.service.announcer;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.AdViewCreateDto;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalViewsAdDto;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.AdViewsEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.announcer.AdRepository;
import org.cunoc.pdfpedia.repository.announcer.AdViewsRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdViewsService {

    private final AdViewsRepository adViewsRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;


    @Transactional
    public void create(@Valid AdViewCreateDto dto){

        AdEntity ad = this.adRepository.findById(dto.adId())
                .orElseThrow(() -> new ValueNotFoundException("Publicacion no encontrada"));

        AdViewsEntity create = AdViewsEntity
                .builder()
                .urlView(dto.urlView())
                .ad(ad).build();

        this.adViewsRepository.save(create);
    }

    public TotalViewsAdDto getTotalViews(Long userId){
        return TotalViewsAdDto
                .builder()
                .total(this.adViewsRepository.countByAd_Advertiser_Id(userId))
                .build();
    }




}
