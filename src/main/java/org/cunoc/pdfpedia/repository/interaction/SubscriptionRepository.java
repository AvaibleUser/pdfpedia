package org.cunoc.pdfpedia.repository.interaction;

import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineSusbcriptions.MagazineProjectionDto;
import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionEntity;
import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

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
}
