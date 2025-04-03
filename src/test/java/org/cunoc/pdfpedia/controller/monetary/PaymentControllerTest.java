package org.cunoc.pdfpedia.controller.monetary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cunoc.pdfpedia.domain.dto.monetary.TotalAmountPaymentByMonthDto;
import org.cunoc.pdfpedia.service.monetary.PaymentService;
import org.cunoc.pdfpedia.util.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.cunoc.pdfpedia.util.JwtBuilder.jwt;
import static org.cunoc.pdfpedia.util.ThenMockAlias.thenMock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiTest(classes = PaymentController.class, controllers = PaymentController.class)
class PaymentControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected PaymentService paymentService;


    @Test
    void getAllPostAdMountTest() throws JsonProcessingException, Exception{
        //Given
        Long userId = 1L;
        TotalAmountPaymentByMonthDto total = new TotalAmountPaymentByMonthDto("03", 25);

        List<TotalAmountPaymentByMonthDto> listTotal = List.of(total);

        String expectedTotalAmount = mapper.writeValueAsString(listTotal);

        given(paymentService.getSumAmountPostAdsByMount(userId)).willReturn(listTotal);

        // when
        ResultActions actualResult = mockMvc.perform(get("/api/v1/payments/investment")
                .contextPath("/api")
                .with(jwt(userId, "EDITOR")));

        // then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(expectedTotalAmount));

        thenMock(paymentService).should().getSumAmountPostAdsByMount(userId);

    }
}