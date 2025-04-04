package org.cunoc.pdfpedia.repository.interaction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.MagazineProjectionCommentsDto;
import org.cunoc.pdfpedia.domain.dto.report.EditorReportData.ReportRow;
import org.cunoc.pdfpedia.domain.dto.report.ReportAggregateData;
import org.cunoc.pdfpedia.domain.entity.interaction.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByMagazineId(Long magazineId);
    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.MagazineProjectionCommentsDto(
        s.magazine.id,
        s.user.username,
        s.magazine.editor.username,
        s.user.email,
        s.magazine.title,
        s.content,
        s.createdAt,
        s.magazine.createdAt)
        FROM comment s
        ORDER BY s.magazine.id
    """)
    List<MagazineProjectionCommentsDto> findAllCommentsDtos();

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.MagazineProjectionCommentsDto(
        s.magazine.id,
        s.user.username,
        s.magazine.editor.username,
        s.user.email,
        s.magazine.title,
        s.content,
        s.createdAt,
        s.magazine.createdAt)
        FROM comment s
        WHERE s.createdAt BETWEEN :startDate AND :endDate
        ORDER BY s.magazine.id
    """)
    List<MagazineProjectionCommentsDto> findAllCommentsDtosBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("""
            SELECT NEW org.cunoc.pdfpedia.domain.dto.report.CommentReportRow(
                c.createdAt,
                c.user.username,
                c.content,
                m.title,
                m.createdAt,
                m.disableComments
            )
            FROM comment c
                JOIN c.magazine m
            WHERE (:magazineId IS NULL OR m.id = :magazineId)
                AND m.editor.id = :editorId
                AND (CAST(:startDate AS LocalDate) IS NULL
                    OR CAST(c.createdAt AS LocalDate) >= :startDate)
                AND (CAST(:endDate AS LocalDate) IS NULL
                    OR CAST(c.createdAt AS LocalDate) <= :endDate)
                AND m.isDeleted = FALSE
            ORDER BY c.createdAt DESC
            """)
    List<ReportRow> reportCommentsByMagazineIdAndBetween(@Param("editorId") Long editorId,
            @Param("magazineId") Long magazineId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT NEW org.cunoc.pdfpedia.domain.dto.report.ReportAggregateData(
                (SELECT COUNT(c) FROM comment c
                    JOIN c.magazine m
                WHERE (:magazineId IS NULL OR m.id = :magazineId)
                    AND m.editor.id = :editorId
                    AND (CAST(:startDate AS LocalDate) IS NULL
                        OR CAST(c.createdAt AS LocalDate) >= :startDate)
                    AND (CAST(:endDate AS LocalDate) IS NULL
                        OR CAST(c.createdAt AS LocalDate) <= :endDate)
                    AND m.isDeleted = FALSE),
                (SELECT AVG(count) FROM (SELECT COUNT(c) AS count FROM comment c
                        JOIN c.magazine m
                    WHERE (:magazineId IS NULL)
                        AND m.editor.id = :editorId
                        AND (CAST(:startDate AS LocalDate) IS NULL
                            OR CAST(c.createdAt AS LocalDate) >= :startDate)
                        AND (CAST(:endDate AS LocalDate) IS NULL
                            OR CAST(c.createdAt AS LocalDate) <= :endDate)
                        AND m.isDeleted = FALSE
                    GROUP BY m.id))
            )
            """)
    ReportAggregateData aggregateCommentsByMagazineIdAndBetween(@Param("editorId") Long editorId,
            @Param("magazineId") Long magazineId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
