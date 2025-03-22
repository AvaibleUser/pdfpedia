package org.cunoc.pdfpedia.repository.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.entity.announcer.AdViewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
