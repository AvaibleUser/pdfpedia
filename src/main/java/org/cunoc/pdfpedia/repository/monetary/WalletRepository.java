package org.cunoc.pdfpedia.repository.monetary;

import org.cunoc.pdfpedia.domain.entity.monetary.WalletEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends CrudRepository<WalletEntity, Long> {

    Optional<WalletEntity> findAllByUserId(Long userId);

}
