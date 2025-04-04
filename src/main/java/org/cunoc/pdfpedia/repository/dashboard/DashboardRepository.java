package org.cunoc.pdfpedia.repository.dashboard;

import java.util.List;

import org.cunoc.pdfpedia.domain.dto.dashboard.ChartPlotDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.ExpiredAdBlockDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.StatsDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.SubscriptionsResumeDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends JpaRepository<MagazineEntity, Long> {

    @Query(nativeQuery = true, value = """
            WITH wallet AS (SELECT w.balance AS total,
                                   s.cm      AS current_month
                    FROM monetary_control.wallet w
                        CROSS JOIN (SELECT generate_series(0, 1)::bool AS cm) s
                    WHERE w.user_id = :editorId),
                likes AS (SELECT COUNT(l.user_id)                                         AS total,
                                 l.created_at::date >= (NOW() - INTERVAL '30 days')::date AS current_month
                    FROM magazine_control.magazine m
                        JOIN interaction_control."like" l
                            ON m.id = l.magazine_id AND l.created_at::date >= (NOW() - INTERVAL '60 days')::date
                    WHERE m.editor_id = :editorId AND m.is_deleted = FALSE
                    GROUP BY current_month),
                comments AS (SELECT COUNT(c.id)                                              AS total,
                                    c.created_at::date >= (NOW() - INTERVAL '30 days')::date AS current_month
                    FROM magazine_control.magazine m
                        JOIN interaction_control.comment c
                            ON m.id = c.magazine_id AND c.created_at::date >= (NOW() - INTERVAL '60 days')::date
                    WHERE m.editor_id = :editorId AND m.is_deleted = FALSE
                    GROUP BY current_month),
                subscriptions AS (SELECT COUNT(s.user_id)                                            AS total,
                                         s.subscribed_at::date >= (NOW() - INTERVAL '30 days')::date AS current_month
                    FROM magazine_control.magazine m
                        JOIN interaction_control.subscription s
                            ON m.id = s.magazine_id AND s.subscribed_at::date >= (NOW() - INTERVAL '60 days')::date
                    WHERE m.editor_id = :editorId
                        AND m.is_deleted = FALSE
                    GROUP BY current_month)
            SELECT w.total                                                                              AS wallet_total,
                   0                                                                                    AS wallet_trend,
                   COALESCE(l.total, 0)                                                                 AS likes_total,
                   -1 + COALESCE(l.total, 0) / LEAD(l.total, 1, 1) OVER (ORDER BY l.current_month DESC) AS likes_trend,
                   COALESCE(c.total, 0)                                                                 AS comments_total,
                   -1 + COALESCE(c.total, 0) / LEAD(c.total, 1, 1) OVER (ORDER BY l.current_month DESC) AS comments_trend,
                   COALESCE(s.total, 0)                                                                 AS subscriptions_total,
                   -1 + COALESCE(s.total, 0) / LEAD(s.total, 1, 1) OVER (ORDER BY l.current_month DESC) AS subscriptions_trend
            FROM wallet w
                     LEFT JOIN likes l USING (current_month)
                     LEFT JOIN comments c USING (current_month)
                     LEFT JOIN subscriptions s USING (current_month)
            ORDER BY w.current_month DESC
            LIMIT 1
            """)
    StatsDto getEditorMonthlyStats(@Param("editorId") long editorId);

    @Query(nativeQuery = true, value = """
            WITH dates AS (SELECT GENERATE_SERIES((NOW() - INTERVAL '30 days')::date, NOW()::date, '1 day'::interval)::date AS group_by),
                likes AS (SELECT l.created_at::date AS group_by, COUNT(l.user_id) AS total
                    FROM interaction_control."like" l
                        JOIN magazine_control.magazine m
                            ON m.id = l.magazine_id AND m.is_deleted = FALSE AND m.editor_id = :editorId
                    GROUP BY group_by),
                comments AS (SELECT c.created_at::date AS group_by, COUNT(c.id) AS total
                    FROM interaction_control.comment c
                        JOIN magazine_control.magazine m
                            ON m.id = c.magazine_id AND m.is_deleted = FALSE AND m.editor_id = :editorId
                    GROUP BY group_by),
                subscriptions AS (SELECT s.subscribed_at::date AS group_by, COUNT(s.user_id) AS total
                    FROM interaction_control.subscription s
                        JOIN magazine_control.magazine m
                            ON m.id = s.magazine_id AND m.is_deleted = FALSE AND m.editor_id = :editorId
                    GROUP BY group_by)
            SELECT d.group_by::text  AS group_by,
                COALESCE(l.total, 0) AS likes,
                COALESCE(c.total, 0) AS comments,
                COALESCE(s.total, 0) AS subscriptions
            FROM dates d
                LEFT JOIN likes l USING (group_by)
                LEFT JOIN comments c USING (group_by)
                LEFT JOIN subscriptions s USING (group_by)
            GROUP BY d.group_by, l.total, c.total, s.total
            ORDER BY d.group_by
            """)
    List<ChartPlotDto> getMonthlyPerformance(@Param("editorId") long editorId);

    @Query(nativeQuery = true, value = """
            WITH likes AS (SELECT m.id, SUM((l.user_id IS NOT NULL)::int) AS total
                FROM magazine_control.magazine m
                    LEFT JOIN interaction_control."like" l
                        ON m.id = l.magazine_id
                WHERE m.editor_id = :editorId AND m.is_deleted = FALSE
                GROUP BY m.id),
                comments AS (SELECT m.id, SUM((c.id IS NOT NULL)::int) AS total
                FROM magazine_control.magazine m
                    LEFT JOIN interaction_control.comment c
                        ON m.id = c.magazine_id
                WHERE m.editor_id = :editorId AND m.is_deleted = FALSE
                GROUP BY m.id),
                subscriptions AS (SELECT m.id, SUM((s.user_id IS NOT NULL)::int) AS total
                FROM magazine_control.magazine m
                    LEFT JOIN interaction_control.subscription s
                        ON m.id = s.magazine_id
                WHERE m.editor_id = :editorId AND m.is_deleted = FALSE
                GROUP BY m.id)
            SELECT m.title                             AS magazine_name,
                   l.total                             AS likes,
                   c.total                             AS comments,
                   s.total                             AS subscriptions
            FROM magazine_control.magazine m
                JOIN likes l USING (id)
                JOIN comments c USING (id)
                JOIN subscriptions s USING (id)
            ORDER BY (l.total + c.total + s.total) / 3.0 DESC
            LIMIT 5
            """)
    List<ChartPlotDto> getTopMagazines(@Param("editorId") long editorId);

    @Query(nativeQuery = true, value = """
            SELECT SUM((NOT s.is_deleted)::int) / COUNT(s.user_id) AS activePercent,
                   SUM(s.is_deleted::int) / COUNT(s.user_id)       AS inactivePercent
            FROM interaction_control.subscription s
                JOIN magazine_control.magazine m
                    ON s.magazine_id = m.id AND m.editor_id = :editorId AND m.is_deleted = FALSE
            """)
    SubscriptionsResumeDto getSubscriptionsResume(@Param("editorId") long editorId);

    @Query("""
            SELECT NEW org.cunoc.pdfpedia.domain.dto.dashboard.ExpiredAdBlockDto(
                m.adBlockingExpirationDate,
                m.title
            )
            FROM magazine m
            WHERE m.editor.id = :editorId
              AND m.isDeleted = FALSE
              AND m.adBlockingExpirationDate <= CAST(NOW() AS LocalDate)
            ORDER BY m.adBlockingExpirationDate DESC
            LIMIT 10
            """)
    List<ExpiredAdBlockDto> getExpiredAdsBlock(@Param("editorId") long editorId);
}
