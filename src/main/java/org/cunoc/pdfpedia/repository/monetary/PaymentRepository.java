package org.cunoc.pdfpedia.repository.monetary;

import org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.type.PaymentType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query("""
    SELECT p FROM payment p
     LEFT JOIN FETCH p.ad a
     LEFT JOIN FETCH a.advertiser LEFT JOIN FETCH a.chargePeriodAd
     WHERE p.paymentType = :paymentType
    """)
    List<PaymentEntity> findByPaymentType(PaymentType paymentType);

    @Query("""
    SELECT p FROM payment p
     LEFT JOIN FETCH p.ad a
     LEFT JOIN FETCH a.advertiser
     LEFT JOIN FETCH a.chargePeriodAd
     WHERE p.paymentType = :paymentType
     AND p.paidAt BETWEEN :startDate AND :endDate
    """)
    List<PaymentEntity> findByPaymentTypeAndDateRange(
            @Param("paymentType") PaymentType paymentType,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query("""
    SELECT p FROM payment p
    LEFT JOIN FETCH p.magazine a
    LEFT JOIN FETCH a.editor
    WHERE p.paymentType = :paymentType
    """)
    List<PaymentEntity> findByPaymentTypeMagazine(PaymentType paymentType);

    @Query("""
    SELECT p FROM payment p
    LEFT JOIN FETCH p.magazine a
    LEFT JOIN FETCH a.editor
    WHERE p.paymentType = :paymentType
    AND p.paidAt BETWEEN :startDate AND :endDate
    """)
    List<PaymentEntity> findByPaymentTypeMagazineAndDateRange(
            @Param("paymentType") PaymentType paymentType,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto(
        TO_CHAR(a.paidAt, 'MM'),
        SUM(a.amount)
    )
    FROM payment a
    WHERE a.ad.advertiser.id = :advertiserId AND a.paymentType = 'POST_AD'
    GROUP BY TO_CHAR(a.paidAt, 'MM')
    ORDER BY TO_CHAR(a.paidAt, 'MM') DESC
    """)
    List<TotalAmountPaymentByMonthDto> sumAmountAdsByMonth(@Param("advertiserId") Long advertiserId);

}
