package org.cunoc.pdfpedia.repository.monetary;

import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query("""
    SELECT NEW org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto(
        TO_CHAR(a.paidAt, 'MM'),
        SUM(a.amount)
    )
    FROM payment a
    WHERE a.ad.advertiser.id = :advertiserId AND a.paymentType = 'POST_AD'
    GROUP BY TO_CHAR(a.paidAt, 'MM')
    ORDER BY TO_CHAR(a.paidAt, 'MM') DESC
""")
    List<TotalAmountPaymentByMonthDto> sumAmountAdsByMonth(@Param("advertiserId") Long advertiserId);

}
