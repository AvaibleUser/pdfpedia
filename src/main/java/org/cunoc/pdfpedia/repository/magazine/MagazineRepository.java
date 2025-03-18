package org.cunoc.pdfpedia.repository.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MagazineRepository extends JpaRepository<MagazineEntity, Long> {

    <T> List<T> findAllByIsDeletedAndEditorId(boolean isDeleted, long editorId, Class<T> type);
}
