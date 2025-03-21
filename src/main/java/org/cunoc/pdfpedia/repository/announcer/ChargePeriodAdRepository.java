package org.cunoc.pdfpedia.repository.announcer;

import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargePeriodAdRepository extends CrudRepository<ChargePeriodAdEntity, Long> {

}
