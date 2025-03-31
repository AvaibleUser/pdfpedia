package org.cunoc.pdfpedia.repository.interaction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.MagazineProjectionDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineItemDto;
import org.cunoc.pdfpedia.domain.dto.report.EditorReportData.ReportRow;
import org.cunoc.pdfpedia.domain.dto.report.ReportAggregateData;
import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionEntity;
import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, SubscriptionId> {

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.MagazineProjectionDto(
        s.magazine.id,
        s.user.username,
        s.magazine.editor.username, s.user.email, s.magazine.title, s.subscribedAt, s.magazine.createdAt)
        FROM subscription s
        WHERE s.isDeleted = false
        ORDER BY s.magazine.id
    """)
    List<MagazineProjectionDto> findAllActiveSubscriptionDtos();

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.MagazineProjectionDto(
        s.magazine.id,
        s.user.username,
        s.magazine.editor.username, s.user.email, s.magazine.title, s.subscribedAt, s.magazine.createdAt)
        FROM subscription s
        WHERE s.isDeleted = false AND s.subscribedAt BETWEEN :startDate AND :endDate
        ORDER BY s.magazine.id
    """)
    List<MagazineProjectionDto> findAllActiveSubscriptionDtosBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT new org.cunoc.pdfpedia.domain.dto.magazine.MagazineItemDto(" +
            "m.id, m.title, m.description, " +
            "new org.cunoc.pdfpedia.domain.dto.category.CategoryDto(" +
            "   m.category.id, m.category.name, m.category.description, " +
            "   m.category.createdAt, m.category.updatedAt), " +
            "new org.cunoc.pdfpedia.domain.dto.user.EditorDto(" +
            "   m.editor.id, m.editor.username, m.editor.email, " +
            "new  org.cunoc.pdfpedia.domain.dto.magazine.EditorProfileDto(" +
            "m.editor.id, m.editor.profile.firstname, m.editor.profile.lastname)), " +
            "SIZE(m.likes), m.disableLikes, m.disableComments, m.disableSuscriptions) " +
            "FROM subscription s " +
            "JOIN s.magazine m " +
            "WHERE s.user.id = :userId AND s.isDeleted = false")
    List<MagazineItemDto> findUserMagazines(@Param("userId") Long userId);

    @Query("""
            SELECT NEW org.cunoc.pdfpedia.domain.dto.report.SubscriptionReportRow(
                s.user.username,
                s.subscribedAt,
                s.isDeleted,
                s.magazine.title,
                s.magazine.createdAt,
                s.magazine.disableSuscriptions
            )
            FROM subscription s
                JOIN s.magazine m
            WHERE (:magazineId IS NULL OR m.id = :magazineId)
                AND m.editor.id = :editorId
                AND (CAST(:startDate AS LocalDate) IS NULL
                    OR CAST(s.subscribedAt AS LocalDate) >= :startDate)
                AND (CAST(:endDate AS LocalDate) IS NULL
                    OR CAST(s.subscribedAt AS LocalDate) <= :endDate)
                AND m.isDeleted = FALSE
            ORDER BY s.subscribedAt DESC
            """)
    List<ReportRow> reportSubscriptionsByMagazineIdAndBetween(@Param("editorId") Long editorId,
            @Param("magazineId") Long magazineId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT NEW org.cunoc.pdfpedia.domain.dto.report.ReportAggregateData(
                (SELECT COUNT(s) FROM subscription s
                    JOIN s.magazine m
                WHERE (:magazineId IS NULL OR m.id = :magazineId)
                    AND m.editor.id = :editorId
                    AND (CAST(:startDate AS LocalDate) IS NULL
                        OR CAST(s.subscribedAt AS LocalDate) >= :startDate)
                    AND (CAST(:endDate AS LocalDate) IS NULL
                        OR CAST(s.subscribedAt AS LocalDate) <= :endDate)
                    AND m.isDeleted = FALSE),
                (SELECT AVG(count) FROM (SELECT COUNT(s) AS count FROM subscription s
                        JOIN s.magazine m
                    WHERE (:magazineId IS NULL)
                        AND m.editor.id = :editorId
                        AND (CAST(:startDate AS LocalDate) IS NULL
                            OR CAST(s.subscribedAt AS LocalDate) >= :startDate)
                        AND (CAST(:endDate AS LocalDate) IS NULL
                            OR CAST(s.subscribedAt AS LocalDate) <= :endDate)
                        AND m.isDeleted = FALSE
                    GROUP BY m.id))
            )
            """)
    ReportAggregateData aggregateSubscriptionsByMagazineIdAndBetween(@Param("editorId") Long editorId,
            @Param("magazineId") Long magazineId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
