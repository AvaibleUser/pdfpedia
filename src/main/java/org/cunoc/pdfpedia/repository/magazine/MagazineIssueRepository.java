package org.cunoc.pdfpedia.repository.magazine;

import org.cunoc.pdfpedia.domain.entity.magazine.MagazineIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface MagazineIssueRepository extends JpaRepository<MagazineIssueEntity, Long> {
    Set<MagazineIssueEntity> findByMagazineId(Long magazineId);
}