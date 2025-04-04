package org.cunoc.pdfpedia.controller.monetary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cunoc.pdfpedia.domain.dto.monetary.WalletDto;
import org.cunoc.pdfpedia.service.monetary.WalletService;
import org.cunoc.pdfpedia.util.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.http.MediaType.APPLICATION_JSON;


import java.math.BigDecimal;

import static org.cunoc.pdfpedia.util.JwtBuilder.jwt;
import static org.cunoc.pdfpedia.util.ThenMockAlias.thenMock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiTest(classes = WalletController.class, controllers = WalletController.class)
class WalletControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected WalletService walletService;

    @Test
    void getWalletByUserIdTest() throws Exception {
        // Given
        Long userId = 1L;
        WalletDto walletDto = new WalletDto(1L, BigDecimal.valueOf(100));
        String expectedWallet = mapper.writeValueAsString(walletDto);

        given(walletService.findUserById(userId)).willReturn(walletDto);

        // When
        ResultActions actualResult = mockMvc.perform(get("/api/v1/wallets/byUser")
                .contextPath("/api")
                .with(jwt(userId, "EDITOR")));

        // Then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(expectedWallet));

        thenMock(walletService).should().findUserById(userId);
    }

    @Test
    void updateWalletTest() throws Exception {
        // Given
        Long walletId = 1L;
        WalletDto walletDto = new WalletDto(walletId, BigDecimal.valueOf(200));
        String walletJson = mapper.writeValueAsString(walletDto);

        given(walletService.updateIncrease(walletId, walletDto)).willReturn(walletDto);

        // When
        ResultActions actualResult = mockMvc.perform(put("/api/v1/wallets/{id}", walletId)
                .contentType(APPLICATION_JSON)
                .content(walletJson)
                .contextPath("/api")
                .with(jwt(1L, "EDITOR")));

        // Then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(walletJson));

        thenMock(walletService).should().updateIncrease(walletId, walletDto);
    }

}