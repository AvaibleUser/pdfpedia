package org.cunoc.pdfpedia.repository.magazine;

import java.util.List;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.entity.magazine.MagazineIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<MagazineIssueEntity, Long> {

    <T> Optional<T> findByIdAndMagazineId(long id, long magazineId, Class<T> type);

    <T> Optional<T> findByIdAndMagazineIdAndMagazineEditorIdAndIsDeletedFalse(long id, long magazineId, long editorId, Class<T> type);

    <T> List<T> findAllByMagazineIdAndIsDeletedFalse(long magazineId, Class<T> type);

    void deleteByIdAndMagazineIdAndMagazineEditorId(long id, long magazineId, long magazineEditorId);
}
