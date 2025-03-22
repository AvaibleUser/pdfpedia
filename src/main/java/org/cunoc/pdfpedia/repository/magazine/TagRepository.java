package org.cunoc.pdfpedia.repository.magazine;

import java.util.List;
import java.util.Set;

import org.cunoc.pdfpedia.domain.entity.magazine.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    <T> List<T> findAllTagsBy(Class<T> type);

    boolean existsAllByIdIn(Iterable<Long> ids);

    Set<TagEntity> findAllByIdIn(Iterable<Long> ids);
}
