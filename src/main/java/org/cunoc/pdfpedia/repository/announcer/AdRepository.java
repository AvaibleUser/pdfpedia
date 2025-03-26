package org.cunoc.pdfpedia.repository.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.dashboard.PostAdMothDto;
import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<AdEntity, Long> {

    List<AdEntity> findAllByAdvertiserIdOrderByExpiresAtDesc(Long id);
    Integer countAllByAdvertiserId(Long id);
    Integer countAllByAdvertiserIdAndIsActiveTrue(Long id);
    List<AdEntity> findAllByAdvertiserIdAndIsActiveTrueAndCreatedAtBetweenOrderByExpiresAtDesc(Long id, LocalDate startDate, LocalDate endDate);
    List<AdEntity> findAllByAdvertiserIdAndIsActiveTrueOrderByExpiresAtDesc(Long id);
    List<AdEntity> findAllByOrderByExpiresAtDesc();

    @EntityGraph(attributePaths = {"viewAds", "chargePeriodAd"})
    List<AdEntity> findByAdvertiser_Id(Long advertiserId);

    long countAllByCreatedAtBetween(Instant createdAt, Instant createdAt2);

    Page<AdEntity> findAllByIsDeletedFalseOrderByAdvertiser(Pageable pageable);

    Page<AdEntity> findAllByIsDeletedFalseAndCreatedAtBetweenOrderByAdvertiser(Instant startDate, Instant endDate,Pageable pageable);

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

    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount(
        TO_CHAR(a.createdAt, 'MM'),
        COUNT(a)
    )
    FROM ad a
    GROUP BY TO_CHAR(a.createdAt, 'MM')
    ORDER BY TO_CHAR(a.createdAt, 'MM') DESC
    """)
    List<PostAdMount> countAdsByMonth();

    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount(
        TO_CHAR(a.createdAt, 'MM'),
        COUNT(a)
    )
    FROM ad a
    WHERE a.createdAt BETWEEN :startDate AND :endDate
    GROUP BY TO_CHAR(a.createdAt, 'MM')
    ORDER BY TO_CHAR(a.createdAt, 'MM') DESC
    """)
    List<PostAdMount> countAdsByMonthByBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);



}
