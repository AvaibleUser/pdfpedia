package org.cunoc.pdfpedia.service.monetary;


import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.type.PaymentType;
import org.cunoc.pdfpedia.repository.monetary.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void createPaymentPostAd(BigDecimal amount, AdEntity ad){
        PaymentEntity paymentEntity = PaymentEntity
                .builder()
                .amount(amount)
                .ad(ad)
                .paymentType(PaymentType.POST_AD)
                .build();

        paymentRepository.save(paymentEntity);
    }
}
