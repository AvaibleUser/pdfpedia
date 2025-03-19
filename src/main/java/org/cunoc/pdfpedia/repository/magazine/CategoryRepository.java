package org.cunoc.pdfpedia.repository.magazine;

import java.util.List;

import org.cunoc.pdfpedia.domain.entity.magazine.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    <T> List<T> findAllCategoriesBy(Class<T> type);

    boolean existsByName(String name);
}
