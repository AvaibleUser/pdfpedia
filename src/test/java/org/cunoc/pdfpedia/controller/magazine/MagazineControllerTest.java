package org.cunoc.pdfpedia.controller.magazine;

import static org.cunoc.pdfpedia.util.JwtBuilder.jwt;
import static org.cunoc.pdfpedia.util.ThenMockAlias.thenMock;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineCounts;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineEditorPreviewDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MinimalMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.UpdateMagazineBlockDto;
import org.cunoc.pdfpedia.domain.dto.magazine.UpdateMagazineDto;
import org.cunoc.pdfpedia.service.magazine.MagazineService;
import org.cunoc.pdfpedia.util.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApiTest(classes = MagazineController.class, controllers = MagazineController.class)
public class MagazineControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    private MagazineService magazineService;

    @Test
    void canFindCompleteEditorMagazines() throws JsonProcessingException, Exception {
        // given
        long editorId = 501L;

        List<MagazineEditorPreviewDto> magazines = List.of(
                MagazineEditorPreviewDto.builder()
                        .id(150L)
                        .title("amazing title")
                        .description("yeah an amazing description")
                        .costPerDay(BigDecimal.valueOf(100))
                        .categoryName("category name")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .counts(MagazineCounts.builder()
                                .issuesCount(100L)
                                .subscriptionsCount(10L)
                                .likesCount(10L)
                                .commentsCount(10L)
                                .tagNames("tag1,tag2")
                                .build())
                        .build(),
                MagazineEditorPreviewDto.builder()
                        .id(151L)
                        .title("amazing title 2")
                        .description("yeah an amazing description 2")
                        .costPerDay(BigDecimal.valueOf(200))
                        .categoryName("category name 2")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .counts(MagazineCounts.builder()
                                .issuesCount(200L)
                                .subscriptionsCount(20L)
                                .likesCount(20L)
                                .commentsCount(20L)
                                .tagNames("tag3,tag4")
                                .build())
                        .build());

        List<Map<String, Object>> expectedMagazines = magazines.stream()
                .map(magazine -> Map.<String, Object>ofEntries(
                        Map.entry("id", magazine.id()),
                        Map.entry("title", magazine.title()),
                        Map.entry("description", magazine.description()),
                        Map.entry("costPerDay", magazine.costPerDay()),
                        Map.entry("categoryName", magazine.categoryName()),
                        Map.entry("createdAt", magazine.createdAt()),
                        Map.entry("updatedAt", magazine.updatedAt()),
                        Map.entry("issuesCount", magazine.counts().issuesCount()),
                        Map.entry("subscriptionsCount", magazine.counts().subscriptionsCount()),
                        Map.entry("likesCount", magazine.counts().likesCount()),
                        Map.entry("commentsCount", magazine.counts().commentsCount()),
                        Map.entry("tagNames", magazine.counts().tagNames().split(","))))
                .toList();

        given(magazineService.findEditorMagazines(editorId)).willReturn(magazines);

        // when
        ResultActions actualResult = mockMvc.perform(get("/api/v1/magazines")
                .contextPath("/api")
                .param("type", "published")
                .param("complete", "true")
                .with(jwt(editorId, "EDITOR")));

        // then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedMagazines)));

        thenMock(magazineService).should().findEditorMagazines(editorId);
    }

    @Test
    void canFindMinimalEditorMagazines() throws JsonProcessingException, Exception {
        // given
        long editorId = 501L;

        List<MinimalMagazineDto> magazines = List.of(
                MinimalMagazineDto.builder()
                        .id(150L)
                        .title("amazing title")
                        .build(),
                MinimalMagazineDto.builder()
                        .id(151L)
                        .title("amazing title 2")
                        .build());

        String expectedMagazines = mapper.writeValueAsString(magazines);

        given(magazineService.findMinimalEditorMagazines(editorId)).willReturn(magazines);

        // when
        ResultActions actualResult = mockMvc.perform(get("/api/v1/magazines")
                .contextPath("/api")
                .param("type", "published")
                .param("complete", "false")
                .with(jwt(editorId, "EDITOR")));

        // then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(expectedMagazines));

        thenMock(magazineService).should().findMinimalEditorMagazines(editorId);
    }

    @Test
    void canFindEditorMagazine() throws JsonProcessingException, Exception {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        MagazineDto magazine = MagazineDto.builder()
                .id(magazineId)
                .title("amazing title")
                .description("yeah an amazing description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .categoryId(150L)
                .tagIds("10,40")
                .build();

        String expectedMagazine = mapper.writeValueAsString(magazine);

        given(magazineService.findEditorMagazine(editorId, magazineId)).willReturn(magazine);

        // when
        ResultActions actualResult = mockMvc.perform(get("/api/v1/magazines/{id}", magazineId)
                .contextPath("/api")
                .param("type", "published")
                .with(jwt(editorId, "EDITOR")));

        // then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(expectedMagazine));

        thenMock(magazineService).should().findEditorMagazine(editorId, magazineId);
    }

    @Test
    void canCreateMagazine() throws JsonProcessingException, Exception {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        AddMagazineDto magazine = AddMagazineDto.builder()
                .title("amazing title")
                .description("yeah an amazing description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .categoryId(150L)
                .tagIds(List.of(10L, 40L))
                .build();

        MinimalMagazineDto expectedMagazine = new MinimalMagazineDto(magazineId, magazine.title());

        given(magazineService.saveMagazine(eq(editorId), refEq(magazine))).willReturn(expectedMagazine);

        // when
        ResultActions actualResult = mockMvc.perform(post("/api/v1/magazines")
                .contextPath("/api")
                .with(jwt(editorId, "EDITOR"))
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(magazine)));

        // then
        actualResult.andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(expectedMagazine)));

        thenMock(magazineService).should().saveMagazine(editorId, magazine);
    }

    @Test
    void canUpdateMagazine() throws JsonProcessingException, Exception {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        UpdateMagazineDto magazine = UpdateMagazineDto.builder()
                .title(Optional.of("amazing title modified"))
                .description(Optional.of("yeah an amazing description modified"))
                .disableLikes(Optional.empty())
                .disableComments(Optional.empty())
                .disableSuscriptions(Optional.empty())
                .categoryId(Optional.of(150L))
                .tagIds(Optional.of(List.of(10L, 40L)))
                .build();

        MinimalMagazineDto expectedMagazine = new MinimalMagazineDto(magazineId, "amazing title modified");

        given(magazineService.updateMagazine(eq(editorId), eq(magazineId), refEq(magazine)))
                .willReturn(expectedMagazine);

        // when
        ResultActions actualResult = mockMvc.perform(put("/api/v1/magazines/{id}", magazineId)
                .contextPath("/api")
                .with(jwt(editorId, "EDITOR"))
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(magazine)));

        // then
        thenMock(magazineService).should().updateMagazine(editorId, magazineId, magazine);
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedMagazine)));

    }

    @Test
    void canUpdateMagazineAds() throws JsonProcessingException, Exception {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        UpdateMagazineBlockDto magazine = new UpdateMagazineBlockDto(LocalDate.now().plusDays(2));

        MinimalMagazineDto expectedMagazine = new MinimalMagazineDto(magazineId, "amazing title modified");

        given(magazineService.updateMagazineAds(eq(editorId), eq(magazineId), refEq(magazine)))
                .willReturn(expectedMagazine);

        // when
        ResultActions actualResult = mockMvc.perform(patch("/api/v1/magazines/{id}/ads", magazineId)
                .contextPath("/api")
                .with(jwt(editorId, "EDITOR"))
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(magazine)));

        // then
        actualResult.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedMagazine)));

        thenMock(magazineService).should().updateMagazineAds(editorId, magazineId, magazine);
    }

    @Test
    void canDeleteMagazine() throws JsonProcessingException, Exception {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        // when
        ResultActions actualResult = mockMvc.perform(delete("/api/v1/magazines/{id}", magazineId)
                .contextPath("/api")
                .with(jwt(editorId, "EDITOR")));

        // then
        actualResult.andExpect(status().isNoContent());

        thenMock(magazineService).should().deleteMagazine(editorId, magazineId);
    }
}
