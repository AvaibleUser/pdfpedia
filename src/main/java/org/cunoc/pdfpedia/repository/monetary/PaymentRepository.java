package org.cunoc.pdfpedia.repository.monetary;

import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.AdReportEmailDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.PaymentPostAdPerAnnouncerDto;
import org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.type.AdType;
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
     LEFT JOIN FETCH a.advertiser LEFT JOIN FETCH a.chargePeriodAd
     WHERE p.paymentType = :paymentType
     AND a.chargePeriodAd.adType = :adType
    """)
    List<PaymentEntity> findByPaymentTypeAndAdType(@Param("paymentType") PaymentType paymentType, @Param("adType") AdType adType);

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
            @Param("endDate") Instant endDate
    );

    @Query("""
    SELECT p FROM payment p
    LEFT JOIN FETCH p.ad a
    LEFT JOIN FETCH a.advertiser LEFT JOIN FETCH a.chargePeriodAd
    WHERE p.paymentType = :paymentType
    AND p.paidAt BETWEEN :startDate AND :endDate
    AND a.chargePeriodAd.adType = :adType
    """)
    List<PaymentEntity> findByPaymentTypeAndAdTypeAndDateRange(
            @Param("paymentType") PaymentType paymentType,
            @Param("adType") AdType adType,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );


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


    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.PaymentPostAdPerAnnouncerDto(
            SUM(p.amount),
            NUll,
            u.username
        )
        FROM payment p
        JOIN p.ad a
        JOIN a.advertiser u
        WHERE p.paymentType = 'POST_AD'
        GROUP BY u.username
    """)
    List<PaymentPostAdPerAnnouncerDto> findGroupedPaymentsByPaymentType();

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.AdReportEmailDto(
            a.chargePeriodAd.adType,
            u.username,
            p.paidAt,
            a.chargePeriodAd.durationDays,
            p.amount,
            u.email
        )
        FROM payment p
        JOIN p.ad a
        JOIN a.advertiser u
        WHERE p.paymentType = 'POST_AD'
    """)
    List<AdReportEmailDto> findAdReportsByPaymentType();


    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.PaymentPostAdPerAnnouncerDto(
            SUM(p.amount),
            NUll,
            u.username
        )
        FROM payment p
        JOIN p.ad a
        JOIN a.advertiser u
        WHERE p.paymentType = 'POST_AD' AND p.paidAt BETWEEN :startDate AND :endDate
        GROUP BY u.username
    """)
    List<PaymentPostAdPerAnnouncerDto> findGroupedPaymentsByPaymentTypeAndBetween(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.AdReportEmailDto(
            a.chargePeriodAd.adType,
            u.username,
            p.paidAt,
            a.chargePeriodAd.durationDays,
            p.amount,
            u.email
        )
        FROM payment p
        JOIN p.ad a
        JOIN a.advertiser u
        WHERE p.paymentType = 'POST_AD' AND p.paidAt BETWEEN :startDate AND :endDate
    """)
    List<AdReportEmailDto> findAdReportsByPaymentTypeAndBetween(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );


    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.PaymentPostAdPerAnnouncerDto(
            SUM(p.amount),
            NUll,
            u.username
        )
        FROM payment p
        JOIN p.ad a
        JOIN a.advertiser u
        WHERE p.paymentType = 'POST_AD' AND a.advertiser.id  = :advertiserId
        GROUP BY u.username
    """)
    List<PaymentPostAdPerAnnouncerDto> findGroupedPaymentsByPaymentTypeByIdUser(@Param("advertiserId") Long advertiserId);

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.AdReportEmailDto(
            a.chargePeriodAd.adType,
            u.username,
            p.paidAt,
            a.chargePeriodAd.durationDays,
            p.amount,
            u.email
        )
        FROM payment p
        JOIN p.ad a
        JOIN a.advertiser u
        WHERE p.paymentType = 'POST_AD' AND a.advertiser.id  = :advertiserId
    """)
    List<AdReportEmailDto> findAdReportsByPaymentTypeByIdUser(@Param("advertiserId") Long advertiserId);


    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.PaymentPostAdPerAnnouncerDto(
            SUM(p.amount),
            NUll,
            u.username
        )
        FROM payment p
        JOIN p.ad a
        JOIN a.advertiser u
        WHERE p.paymentType = 'POST_AD' AND a.advertiser.id  = :advertiserId AND p.paidAt BETWEEN :startDate AND :endDate
        GROUP BY u.username
    """)
    List<PaymentPostAdPerAnnouncerDto> findGroupedPaymentsByPaymentTypeAndBetweenById(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("advertiserId") Long advertiserId
    );

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.AdReportEmailDto(
            a.chargePeriodAd.adType,
            u.username,
            p.paidAt,
            a.chargePeriodAd.durationDays,
            p.amount,
            u.email
        )
        FROM payment p
        JOIN p.ad a
        JOIN a.advertiser u
        WHERE p.paymentType = 'POST_AD' AND a.advertiser.id  = :advertiserId AND p.paidAt BETWEEN :startDate AND :endDate
    """)
    List<AdReportEmailDto> findAdReportsByPaymentTypeAndBetweenById(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("advertiserId") Long advertiserId
    );




}
