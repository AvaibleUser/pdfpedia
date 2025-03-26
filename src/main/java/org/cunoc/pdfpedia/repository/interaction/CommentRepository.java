package org.cunoc.pdfpedia.repository.interaction;

import org.cunoc.pdfpedia.domain.entity.interaction.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByMagazineId(Long magazineId);
}