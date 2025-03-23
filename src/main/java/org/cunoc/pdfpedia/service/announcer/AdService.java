package org.cunoc.pdfpedia.service.announcer;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.*;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.domain.utils.MapperAd;
import org.cunoc.pdfpedia.repository.announcer.AdRepository;
import org.cunoc.pdfpedia.repository.announcer.ChargePeriodAdRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.cunoc.pdfpedia.service.monetary.PaymentService;
import org.cunoc.pdfpedia.service.monetary.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final ChargePeriodAdRepository chargePeriodAdRepository;
    private final MapperAd mapperAd;
    private final WalletService walletService;
    private final PaymentService paymentService;

    @Transactional
    public AdDto create(@Valid AdPostDto adPostDto, Long advertiserId) {
        UserEntity advertiser = userRepository.findById(advertiserId)
                .orElseThrow(() -> new ValueNotFoundException("Usuario no encontrado al realizar una publicación de anuncio"));

        ChargePeriodAdEntity chargePeriodAd = chargePeriodAdRepository.findById(adPostDto.chargePeriodAd())
                .orElseThrow(() -> new ValueNotFoundException("Periodo de vigencia no válido al realizar una publicación de anuncio"));

        AdEntity entity = mapperAd.toEntity(adPostDto, advertiser, chargePeriodAd);

        // Descontar de cartera digital, validar si le alcanza el saldo
        this.walletService.updateDecrease(advertiserId, chargePeriodAd.getCost());

        AdEntity savedEntity = adRepository.save(entity);

        //registrar el pago
        this.paymentService.createPaymentPostAd(chargePeriodAd.getCost(), savedEntity);

        return mapperAd.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<AdDto> findAllByUserId(Long userId) {
        return this.adRepository.findAllByAdvertiserIdOrderByExpiresAtDesc(userId)
                .stream()
                .map(this.mapperAd::toDto)
                .toList();
    }

    public List<AdDto> findAllActiveByUserId(LocalDate startDate, LocalDate endDate, Long userId) {
        if (startDate == null && endDate == null){
            return this.adRepository.findAllByAdvertiserIdAndIsActiveTrueOrderByExpiresAtDesc(userId)
                    .stream()
                    .map(this.mapperAd::toDto)
                    .toList();
        }
        return this.adRepository.findAllByAdvertiserIdAndIsActiveTrueAndCreatedAtBetweenOrderByExpiresAtDesc(userId, startDate, endDate)
                .stream()
                .map(this.mapperAd::toDto)
                .toList();

    }

    @Transactional
    public AdDto updateDeactivated(Long adId) {
        AdEntity exit = this.adRepository.findById(adId).
                orElseThrow(() -> new ValueNotFoundException("Anuncio no encontrado para desactivar"));
        exit.setActive(false);

        this.adRepository.save(exit);

        return this.mapperAd.toDto(exit);
    }

    @Transactional
    public AdDto updateActive(Long adId, @Valid AdPostDto adPostDto, Long advertiserId) {
        ChargePeriodAdEntity chargePeriodAd = chargePeriodAdRepository.findById(adPostDto.chargePeriodAd())
                .orElseThrow(() -> new ValueNotFoundException("Periodo de vigencia no válido al realizar una publicación de anuncio"));

        AdEntity exit = this.adRepository.findById(adId).
                orElseThrow(() -> new ValueNotFoundException("Anuncio no encontrado para activar"));

        exit.setActive(true);
        exit.setExpiresAt(LocalDateTime.now().plusDays(chargePeriodAd.getDurationDays()));
        exit.setChargePeriodAd(chargePeriodAd);

        // Descontar de cartera digital, validar si le alcanza el saldo
        this.walletService.updateDecrease(advertiserId, chargePeriodAd.getCost());

        AdEntity savedEntity = adRepository.save(exit);

        //registrar el pago
        this.paymentService.createPaymentPostAd(chargePeriodAd.getCost(), savedEntity);

        return mapperAd.toDto(savedEntity);

    }

    @Transactional
    public AdDto update(Long adId, @Valid AdUpdateDto adUpdateDto) {
        AdEntity exist = this.adRepository.findById(adId).
                orElseThrow(() -> new ValueNotFoundException("Anuncio no encontrado para activar"));

        exist.setContent(adUpdateDto.content());
        exist.setVideoUrl(adUpdateDto.videoUrl());
        exist.setImageUrl(adUpdateDto.imageUrl());

        AdEntity savedEntity = adRepository.save(exist);

        return mapperAd.toDto(savedEntity);
    }

    public TotalAdsDto totalAdsByUserId(Long userId) {
        return new TotalAdsDto(this.adRepository.countAllByAdvertiserId(userId)
                , this.adRepository.countAllByAdvertiserIdAndIsActiveTrue(userId));
    }

    public List<PostAdMount> getPostMount(Long userId) {
        return adRepository.countAdsByMonth(userId);
    }

}
