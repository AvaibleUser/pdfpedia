package org.cunoc.pdfpedia.repository.magazine;

import java.time.Instant;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MagazineRepository extends JpaRepository<MagazineEntity, Long> {

    <T> List<T> findAllByIsDeletedAndEditorId(boolean isDeleted, long editorId, Class<T> type);

    long countAllByCreatedAtBetween(Instant createdAt, Instant createdAt2);

    Page<MagazineEntity> findAllByIsDeletedFalseOrderByEditor(Pageable pageable);

    Page<MagazineEntity> findAllByIsDeletedFalseAndCreatedAtBetweenOrderByEditor(Instant startDate, Instant endDate, Pageable pageable);

    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount(
        TO_CHAR(a.createdAt, 'MM'),
        COUNT(a)
    )
    FROM magazine a
    GROUP BY TO_CHAR(a.createdAt, 'MM')
    ORDER BY TO_CHAR(a.createdAt, 'MM') DESC
    """)
    List<PostAdMount> countMagazineByMonth();

    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount(
        TO_CHAR(a.createdAt, 'MM'),
        COUNT(a)
    )
    FROM magazine a
    WHERE a.createdAt BETWEEN :startDate AND :endDate
    GROUP BY TO_CHAR(a.createdAt, 'MM')
    ORDER BY TO_CHAR(a.createdAt, 'MM') DESC
    """)
    List<PostAdMount> countMagazineByMonthByBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);



}
