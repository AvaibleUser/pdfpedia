package org.cunoc.pdfpedia.repository.interaction;


import org.cunoc.pdfpedia.domain.dto.admin.report.topMagazineCommets.MagazineProjectionCommentsDto;
import org.cunoc.pdfpedia.domain.entity.interaction.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

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
}