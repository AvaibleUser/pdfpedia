package org.cunoc.pdfpedia.service.announcer;

import jakarta.validation.Valid;
import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.domain.dto.dashboard.AnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.magazine.TopEditorDto;

import java.time.LocalDate;
import java.util.List;

public interface IAdService {

    AdDto create(@Valid AdPostDto adPostDto, Long advertiserId);

    List<AdDto> findAllByUserId(Long userId);

    List<AdDto> findAllActiveByUserId(LocalDate startDate, LocalDate endDate, Long userId);

    AdDto updateDeactivated(Long adId);

    AdDto updateActive(Long adId, @Valid AdPostDto adPostDto, Long advertiserId);

    AdDto update(Long adId, @Valid AdUpdateDto adUpdateDto);

    TotalAdsDto totalAdsByUserId(Long userId);

    List<PostAdMount> getPostMount(Long userId);

    TotalTarjertDto getTotalPostAd(LocalDate startDate, LocalDate endDate);

    TopEditorDto getTopPostAd(LocalDate startDate, LocalDate endDate);

    List<PostAdMount> getAdCountsByMonth(LocalDate startDate, LocalDate endDate);

    List<AdDto> findAll();

    List<AnnouncersDto> findAllAnnouncers();

}
