package org.cunoc.pdfpedia.repository.announcer;

import org.cunoc.pdfpedia.domain.dto.announcer.CountAdByTypeDto;
import org.cunoc.pdfpedia.domain.entity.announcer.ChargePeriodAdEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargePeriodAdRepository extends CrudRepository<ChargePeriodAdEntity, Long> {

    @Query("""
        SELECT new org.cunoc.pdfpedia.domain.dto.announcer.CountAdByTypeDto(c.adType, COUNT(a))
        FROM ad a
        JOIN a.chargePeriodAd c
        WHERE a.advertiser.id = :advertiserId
        GROUP BY c.adType
    """)
    List<CountAdByTypeDto> countAdsByTypeForUser(@Param("advertiserId") Long advertiserId);
}
