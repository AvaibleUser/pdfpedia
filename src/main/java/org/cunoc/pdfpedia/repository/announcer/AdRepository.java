package org.cunoc.pdfpedia.repository.announcer;

import org.cunoc.pdfpedia.domain.entity.announcer.AdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRepository extends JpaRepository<AdEntity, Long> {

}
