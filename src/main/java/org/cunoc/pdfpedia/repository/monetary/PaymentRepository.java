package org.cunoc.pdfpedia.repository.monetary;

import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
