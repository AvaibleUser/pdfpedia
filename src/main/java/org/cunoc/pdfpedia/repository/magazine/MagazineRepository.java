package org.cunoc.pdfpedia.repository.magazine;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineEditorPreviewDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazinePreviewDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MagazineRepository extends JpaRepository<MagazineEntity, Long> {

    <T> List<T> findAllByIsDeletedAndEditorId(boolean isDeleted, long editorId, Class<T> type);

    long countAllByCreatedAtBetween(Instant createdAt, Instant createdAt2);

    Page<MagazineEntity> findAllByIsDeletedFalseOrderByEditor(Pageable pageable);

    Page<MagazineEntity> findAllByIsDeletedFalseAndCreatedAtBetweenOrderByEditor(Instant startDate, Instant endDate,
            Pageable pageable);

    <T> List<T> findAllSimpleByEditorIdAndIsDeletedFalseOrderById(@Param("editorId") long editorId, Class<T> type);

    void deleteByIdAndEditorId(long id, long editorId);

    <T> Optional<T> findByIdAndEditorIdAndIsDeletedFalse(long id, long editorId, Class<T> type);


    List<MagazineEntity> findAll(Sort sort);

    List<MagazineEntity> findAllByCostPerDayIsNullOrderByCreatedAt(Sort sort);

    List<MagazineEntity> findAllByCostPerDayIsNullAndEditor_IdOrderByCreatedAt(Long editorId, Sort sort);

    List<MagazineEntity> findAllByEditor_IdOrderByCreatedAt(Long editorId, Sort sort);


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
    List<PostAdMount> countMagazineByMonthByBetween(@Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query("""
            SELECT new org.cunoc.pdfpedia.domain.dto.magazine.MagazineDto(
                m.id,
                m.title,
                m.description,
                m.adBlockingExpirationDate,
                m.disableLikes,
                m.disableComments,
                m.disableSuscriptions,
                m.category.id,
                (SELECT CAST(STRING_AGG(CAST(t.id AS String), ',') AS String) FROM m.tags t)
            )
            FROM magazine m
            WHERE m.id = :id
                AND m.editor.id = :editorId
                AND m.isDeleted = FALSE
                    """)
    Optional<MagazineDto> findByIdAndEditorIdAndIsDeletedFalse(@Param("id") long id, @Param("editorId") long editorId);

    @Query("""
            SELECT new org.cunoc.pdfpedia.domain.dto.magazine.MagazineEditorPreviewDto(
                m.id,
                m.title,
                m.description,
                m.costPerDay,
                m.category.name,
                m.createdAt,
                m.updatedAt,
                new org.cunoc.pdfpedia.domain.dto.magazine.MagazineCounts(
                    (SELECT COUNT(i) FROM m.issues          i WHERE i.isDeleted = FALSE),
                    (SELECT COUNT(s) FROM m.subscriptions   s WHERE s.isDeleted = FALSE),
                    (SELECT COUNT(l) FROM m.likes           l),
                    (SELECT COUNT(c) FROM m.comments        c),
                    (SELECT CAST(STRING_AGG(t.name, ',') AS String) FROM m.tags t)
                )
            )
            FROM magazine m
            WHERE m.editor.id = :editorId
                AND m.isDeleted = FALSE
                    """)
    List<MagazineEditorPreviewDto> findAllByEditorIdAndIsDeletedFalse(@Param("editorId") long editorId);

    @Query("""
            SELECT new org.cunoc.pdfpedia.domain.dto.magazine.MagazinePreviewDto(
                m.id,
                m.title,
                m.description,
                m.editor.username,
                m.category.name,
                m.createdAt,
                m.updatedAt,
                new org.cunoc.pdfpedia.domain.dto.magazine.MagazineCounts(
                    (SELECT COUNT(i) FROM m.issues          i WHERE i.isDeleted = FALSE),
                    (SELECT COUNT(s) FROM m.subscriptions   s WHERE s.isDeleted = FALSE),
                    (SELECT COUNT(l) FROM m.likes           l),
                    (SELECT COUNT(c) FROM m.comments        c),
                    (SELECT CAST(STRING_AGG(t.name, ',') AS String) FROM m.tags t)
                )
            )
            FROM magazine m
            WHERE m.costPerDay IS NOT NULL
                AND m.isDeleted = FALSE
                    """)
    List<MagazinePreviewDto> findAllByIsDeletedFalseAndCostPerDayIsNotNull();
}
