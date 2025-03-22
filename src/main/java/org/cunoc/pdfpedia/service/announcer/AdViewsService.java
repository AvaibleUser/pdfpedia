package org.cunoc.pdfpedia.service.announcer;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.AdViewsEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.domain.utils.MapperAd;
import org.cunoc.pdfpedia.repository.announcer.AdRepository;
import org.cunoc.pdfpedia.repository.announcer.AdViewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdViewsService {

    private final AdViewsRepository adViewsRepository;
    private final AdRepository adRepository;
    private final MapperAd mapperAd;

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

    public List<PostAdMount> getPostMount(Long userId) {
        return this.adViewsRepository.countViewsAdsByMonth(userId);
    }

    private List<AdViewReportDto> getReportViewsAll(Long userId){
        List<AdEntity> ads = this.adRepository.findByAdvertiser_Id(userId);
        return ads.stream().map(this.mapperAd::adViewsDto).toList();
    }

    private boolean isWithinRange(Instant createdAt, LocalDate startDate, LocalDate endDate) {
        LocalDate createdDate = createdAt.atZone(ZoneId.systemDefault()).toLocalDate();
        return (startDate == null || !createdDate.isBefore(startDate)) &&
                (endDate == null || !createdDate.isAfter(endDate));
    }

    public List<AdViewReportDto> getReportViewsFilterDate(LocalDate startDate, LocalDate endDate, Long userId) {
        List<AdEntity> ads = this.adRepository.findByAdvertiser_Id(userId);

        return ads.stream().map(ad -> {
            List<ViewAdDto> filteredViews = ad.getViewAds().stream()
                    .filter(view -> isWithinRange(view.getCreatedAt(), startDate, endDate))
                    .map(this.mapperAd::toDto)
                    .toList();

            return AdViewReportDto
                    .builder()
                    .adDto(this.mapperAd.toDto(ad))
                    .viewsAdDto(filteredViews)
                    .build();
        }).toList();
    }

    public List<AdViewReportDto> getReportViews(LocalDate startDate, LocalDate endDate, Long userId) {
        if (startDate == null && endDate == null) {
            return this.getReportViewsAll(userId);
        }
        return  this.getReportViewsFilterDate(startDate, endDate, userId);
    }

}
