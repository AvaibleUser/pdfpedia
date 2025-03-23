package org.cunoc.pdfpedia.domain.dto.monetary;


public record TotalAmountPaymentByMonthDto(String month, Long amount) {
    public TotalAmountPaymentByMonthDto(String month, Number amount) {
        this(month, amount.longValue());
    }
}


