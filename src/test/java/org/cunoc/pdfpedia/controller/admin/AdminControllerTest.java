package org.cunoc.pdfpedia.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cunoc.pdfpedia.domain.dto.admin.MagazineAdminDto;
import org.cunoc.pdfpedia.domain.dto.admin.UpdateCostMagazineDto;
import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.dashboard.AnnouncersDto;
import org.cunoc.pdfpedia.service.admin.AdminService;
import org.cunoc.pdfpedia.util.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.cunoc.pdfpedia.util.JwtBuilder.jwt;
import static org.cunoc.pdfpedia.util.ThenMockAlias.thenMock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiTest(classes = AdminController.class, controllers = AdminController.class)
class AdminControllerTest {

        @Autowired
        protected MockMvc mockMvc;

        @Autowired
        protected ObjectMapper mapper;

        @MockitoBean
        protected AdminService adminService;

    @Test
    void getAllMagazinesWithParamsTest() throws Exception {
        // Given
        boolean costNull = false;
        Long editorId = 1L;
        boolean asc = true;
        List<MagazineAdminDto> magazines = List.of(
                new MagazineAdminDto(1L, "Tech Monthly", BigDecimal.valueOf(10), Instant.now(), "editor1"));
        String expectedResponse = mapper.writeValueAsString(magazines);

        given(adminService.getAllMagazinesWithParams(costNull, editorId, asc)).willReturn(magazines);

        // When
        ResultActions actualResult = mockMvc.perform(get("/api/v1/admin/magazines")
                .contextPath("/api")
                .param("costNull", String.valueOf(costNull))
                .param("editorId", String.valueOf(editorId))
                .param("asc", String.valueOf(asc))
                .with(jwt(1L, "ADMIN")));

        // Then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        thenMock(adminService).should().getAllMagazinesWithParams(costNull, editorId, asc);
    }

    @Test
    void updateCostMagazineTest() throws Exception {
        // Given
        Long magazineId = 1L;
        UpdateCostMagazineDto updateDto = new UpdateCostMagazineDto(BigDecimal.valueOf(20));
        String updateJson = mapper.writeValueAsString(updateDto);

        // When
        ResultActions actualResult = mockMvc.perform(put("/api/v1/admin/{id}", magazineId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .contextPath("/api")
                .with(jwt(1L, "ADMIN")));

        // Then
        actualResult.andExpect(status().isOk());

        thenMock(adminService).should().updateCostMagazine(magazineId, updateDto);
    }

    @Test
    void findAllAnnouncersTest() throws Exception {
        // Given
        List<AnnouncersDto> announcers = List.of(new AnnouncersDto(1L, "announcer1"));
        String expectedResponse = mapper.writeValueAsString(announcers);

        given(adminService.findAllEditors()).willReturn(announcers);

        // When
        ResultActions actualResult = mockMvc.perform(get("/api/v1/admin/all-editors")
                .contextPath("/api")
                .with(jwt(1L, "ADMIN")));

        // Then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        thenMock(adminService).should().findAllEditors();
    }

    @Test
    void findAllAnnouncersMonthTest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        List<PostAdMount> totals = List.of(new PostAdMount("01", 10));
        String expectedResponse = mapper.writeValueAsString(totals);

        given(adminService.countRegisterByMonthByBetween(startDate, endDate)).willReturn(totals);

        // When
        ResultActions actualResult = mockMvc.perform(get("/api/v1/admin/total-registers-month")
                .contextPath("/api")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .with(jwt(1L, "ADMIN")));

        // Then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        thenMock(adminService).should().countRegisterByMonthByBetween(startDate, endDate);
    }


}