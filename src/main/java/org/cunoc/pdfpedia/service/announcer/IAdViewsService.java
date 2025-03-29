package org.cunoc.pdfpedia.service.announcer;

import jakarta.validation.Valid;
import org.cunoc.pdfpedia.domain.dto.announcer.AdViewCreateDto;
import org.cunoc.pdfpedia.domain.dto.announcer.AdViewReportDto;
import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.announcer.TotalViewsAdDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface IAdViewsService {

    void create(@Valid AdViewCreateDto dto);

    TotalViewsAdDto getTotalViews(Long userId);

    List<PostAdMount> getPostMount(Long userId);

    List<AdViewReportDto> getReportViewsAll(Long userId);

    boolean isWithinRange(Instant createdAt, LocalDate startDate, LocalDate endDate);

    List<AdViewReportDto> getReportViewsFilterDate(LocalDate startDate, LocalDate endDate, Long userId);

    List<AdViewReportDto> getReportViews(LocalDate startDate, LocalDate endDate, Long userId);
}
