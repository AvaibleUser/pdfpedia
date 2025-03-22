package org.cunoc.pdfpedia.repository.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<AdEntity, Long> {

    List<AdEntity> findAllByAdvertiserIdOrderByExpiresAtDesc(Long id);
    Integer countAllByAdvertiserId(Long id);
    Integer countAllByAdvertiserIdAndIsActiveTrue(Long id);
    List<AdEntity> findAllByAdvertiserIdAndIsActiveTrueAndCreatedAtBetweenOrderByExpiresAtDesc(Long id, LocalDate startDate, LocalDate endDate);
    List<AdEntity> findAllByAdvertiserIdAndIsActiveTrueOrderByExpiresAtDesc(Long id);

    @EntityGraph(attributePaths = {"viewAds", "chargePeriodAd"})
    List<AdEntity> findByAdvertiser_Id(Long advertiserId);

    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount(
        TO_CHAR(a.createdAt, 'MM'),
        COUNT(a)
    )
    FROM ad a
    WHERE a.advertiser.id = :advertiserId
    GROUP BY TO_CHAR(a.createdAt, 'MM')
    ORDER BY TO_CHAR(a.createdAt, 'MM') DESC
    """)
    List<PostAdMount> countAdsByMonth(@Param("advertiserId") Long advertiserId);


}
