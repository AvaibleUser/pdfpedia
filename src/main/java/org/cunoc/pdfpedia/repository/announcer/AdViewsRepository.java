package org.cunoc.pdfpedia.repository.announcer;

import org.cunoc.pdfpedia.domain.entity.announcer.AdViewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdViewsRepository extends JpaRepository<AdViewsEntity, Long> {

    Integer countByAd_Advertiser_Id(Long advertiserId);
}
