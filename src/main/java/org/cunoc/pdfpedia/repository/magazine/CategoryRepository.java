package org.cunoc.pdfpedia.repository.magazine;

import org.cunoc.pdfpedia.domain.entity.magazine.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByName(String name);
}
