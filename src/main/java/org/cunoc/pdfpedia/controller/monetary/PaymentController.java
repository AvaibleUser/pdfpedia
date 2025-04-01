package org.cunoc.pdfpedia.controller.monetary;


import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto;
import org.cunoc.pdfpedia.service.monetary.IPaymentService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @GetMapping("/investment")
    @RolesAllowed({"ANNOUNCER", "EDITOR", "ADMIN"})
    public ResponseEntity<List<TotalAmountPaymentByMonthDto>> getAllPostAdMount(@CurrentUserId long userId){
        return ResponseEntity.status(HttpStatus.OK).body(this.paymentService.getSumAmountPostAdsByMount(userId));
    }

}
