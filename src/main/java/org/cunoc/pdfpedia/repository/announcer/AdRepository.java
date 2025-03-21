package org.cunoc.pdfpedia.repository.announcer;

import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<AdEntity, Long> {

    List<AdEntity> findAllByAdvertiserIdOrderByExpiresAtDesc(Long id);
    Integer countAllByAdvertiserId(Long id);
    Integer countAllByAdvertiserIdAndIsActiveTrue(Long id);
}
