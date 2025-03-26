package org.cunoc.pdfpedia.repository;

import org.cunoc.pdfpedia.domain.entity.ConfigurationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends CrudRepository<ConfigurationEntity, Long> {

    <T> Optional<T> findFirstByOrderByIdAsc(Class<T> type);
}
