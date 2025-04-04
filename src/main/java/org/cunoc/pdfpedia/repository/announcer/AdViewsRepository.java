package org.cunoc.pdfpedia.repository.announcer;

import org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness.ViewsProjection;
import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.entity.announcer.AdViewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AdViewsRepository extends JpaRepository<AdViewsEntity, Long> {

    Integer countByAd_Advertiser_Id(Long advertiserId);

    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount(
        TO_CHAR(a.createdAt, 'MM'),
        COUNT(a)
    )
    FROM ad_view a
    WHERE a.ad.advertiser.id = :advertiserId
    GROUP BY TO_CHAR(a.createdAt, 'MM')
    ORDER BY TO_CHAR(a.createdAt, 'MM') DESC
    """)
    List<PostAdMount> countViewsAdsByMonth(@Param("advertiserId") Long advertiserId);

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness.ViewsProjection(
        v.urlView,
        v.createdAt,
        v.ad.createdAt,
        v.ad.chargePeriodAd.adType,
        v.ad.id,
        v.ad.chargePeriodAd.durationDays,
        v.ad.advertiser.username,
        v.ad.advertiser.email,
        v.ad.advertiser.id)
        FROM ad_view v
        ORDER BY v.ad.id
    """)
    List<ViewsProjection> findAllViewsProjectionReportDto();

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.adEffectiveness.ViewsProjection(
        v.urlView,
        v.createdAt,
        v.ad.createdAt,
        v.ad.chargePeriodAd.adType,
        v.ad.id,
        v.ad.chargePeriodAd.durationDays,
        v.ad.advertiser.username,
        v.ad.advertiser.email,
        v.ad.advertiser.id)
        FROM ad_view v WHERE v.createdAt BETWEEN :startDate AND :endDate
        ORDER BY v.ad.id
    """)
    List<ViewsProjection> findAllViewsProjectionReportDtoRange(
            @Param("startDate") Instant startDate, @Param("endDate") Instant endDate
    );

}
