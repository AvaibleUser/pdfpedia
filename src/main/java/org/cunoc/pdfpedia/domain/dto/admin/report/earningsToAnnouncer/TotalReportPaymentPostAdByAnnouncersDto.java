package org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record TotalReportPaymentPostAdByAnnouncersDto(
        List<PaymentPostAdPerAnnouncerDto> paymentPostAdPerAnnouncerDtos,
        BigDecimal totalAdPost,
        String range,
        String filter

) {
}
