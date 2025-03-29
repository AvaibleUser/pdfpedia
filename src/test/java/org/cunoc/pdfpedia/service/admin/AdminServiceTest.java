package org.cunoc.pdfpedia.service.admin;

import org.cunoc.pdfpedia.domain.dto.admin.MagazineAdminDto;
import org.cunoc.pdfpedia.domain.dto.admin.UpdateCostMagazineDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineCostTotalDto;
import org.cunoc.pdfpedia.domain.dto.announcer.PostAdMount;
import org.cunoc.pdfpedia.domain.dto.dashboard.AnnouncersDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private MagazineRepository magazineRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private MagazineEntity magazine;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("editorUser");
        magazine = new MagazineEntity();
        magazine.setId(1L);
        magazine.setTitle("Tech Magazine");
        magazine.setDescription("Tech Magazine Description");
        magazine.setCostPerDay(BigDecimal.valueOf(10));
        magazine.setEditor(user);
        magazine.setCreatedAt(Instant.now());
    }

    @Test
    void givenValidDates_whenCalculateTotalCost_thenReturnsCorrectCost() {
        // Given
        Instant createdAt = Instant.now().minusSeconds(86400 * 5); // 5 días atrás
        BigDecimal costPerDay = BigDecimal.valueOf(10);
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now();

        // When
        BigDecimal totalCost = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(BigDecimal.valueOf(50), totalCost);
    }

    @Test
    void givenEndDateBeforeCreatedDate_whenCalculateTotalCost_thenReturnsZero() {
        // Given
        Instant createdAt = Instant.now();
        BigDecimal costPerDay = BigDecimal.valueOf(10);
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now().minusDays(2);

        // When
        BigDecimal totalCost = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(BigDecimal.ZERO, totalCost);
    }

    @Test
    void givenMagazineEntity_whenToDto_thenReturnsCorrectDto() {
        // When
        MagazineAdminDto dto = adminService.toDto(magazine);

        // Then
        assertEquals(magazine.getId(), dto.id());
        assertEquals(magazine.getTitle(), dto.title());
        assertEquals(magazine.getCostPerDay(), dto.costPerDay());
    }

    @Test
    void givenMagazineEntity_whenTotalDto_thenReturnsCorrectCostTotalDto() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        // When
        MagazineCostTotalDto dto = adminService.totalDto(magazine, startDate, endDate);

        // Then
        assertEquals(BigDecimal.valueOf(0), dto.costTotal());
    }

    @Test
    void givenEditorId_whenHasValidEditor_thenReturnsTrue() {
        assertTrue(adminService.hasValidEditor(1L));
    }

    @Test
    void givenInvalidEditorId_whenHasValidEditor_thenReturnsFalse() {
        assertFalse(adminService.hasValidEditor(0L));
    }

    @Test
    void givenValidId_whenUpdateCostMagazine_thenUpdatesSuccessfully() {
        // Given
        UpdateCostMagazineDto updateDto = new UpdateCostMagazineDto(BigDecimal.valueOf(20));
        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));

        // When
        adminService.updateCostMagazine(1L, updateDto);

        // Then
        assertEquals(updateDto.costPerDay(), magazine.getCostPerDay());
    }

    @Test
    void givenNoEditors_whenFindAllEditors_thenReturnsEmptyList() {
        when(userRepository.findAllByRole_Name("EDITOR", AnnouncersDto.class)).thenReturn(List.of());
        List<AnnouncersDto> editors = adminService.findAllEditors();
        assertTrue(editors.isEmpty());
    }

    @Test
    void givenValidDates_whenGetAllCostTotalMagazines_thenReturnsList() {
        when(magazineRepository.findAllByCostPerDayIsNotNull()).thenReturn(List.of(magazine));
        List<MagazineCostTotalDto> result = adminService.getAllCostTotalMagazines(null, null);
        assertFalse(result.isEmpty());
    }

    @Test
    void givenValidDateRange_whenCountRegisterByMonthByBetween_thenReturnsList() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        when(userRepository.countRegisterByMonthByBetween(any(), any())).thenReturn(List.of(new PostAdMount("March", 10)));
        List<PostAdMount> result = adminService.countRegisterByMonthByBetween(startDate, endDate);
        assertFalse(result.isEmpty());
    }

    @Test
    void calculateTotalCost_basicCase_shouldCalculateCorrectly() {
        // Given
        Instant createdAt = LocalDate.of(2023, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BigDecimal costPerDay = new BigDecimal("10.00");
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023, 1, 11);

        // When
        BigDecimal result = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void calculateTotalCost_startDateAfterCreated_shouldUseStartDate() {
        // Given
        Instant createdAt = LocalDate.of(2023, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BigDecimal costPerDay = new BigDecimal("5.00");
        LocalDate startDate = LocalDate.of(2023, 1, 6); // 5 days after creation
        LocalDate endDate = LocalDate.of(2023, 1, 11); // 5 days after start date

        // When
        BigDecimal result = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(new BigDecimal("25.00"), result);
    }

    @Test
    void calculateTotalCost_startDateBeforeCreated_shouldIgnoreStartDate() {
        // Given
        Instant createdAt = LocalDate.of(2023, 1, 10).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BigDecimal costPerDay = new BigDecimal("2.00");
        LocalDate startDate = LocalDate.of(2023, 1, 1); // Before creation
        LocalDate endDate = LocalDate.of(2023, 1, 15); // 5 days after creation

        // When
        BigDecimal result = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(new BigDecimal("10.00"), result);
    }

    @Test
    void calculateTotalCost_noEndDate_shouldUseCurrentDate() {
        // Given
        Instant createdAt = LocalDate.now().minusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BigDecimal costPerDay = new BigDecimal("3.00");
        LocalDate startDate = null;
        LocalDate endDate = null;

        // When
        BigDecimal result = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(new BigDecimal("15.00"), result);
    }

    @Test
    void calculateTotalCost_endDateBeforeCreated_shouldReturnZero() {
        // Given
        Instant createdAt = LocalDate.of(2023, 1, 10).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BigDecimal costPerDay = new BigDecimal("4.00");
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023, 1, 5); // Before creation

        // When
        BigDecimal result = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateTotalCost_zeroCostPerDay_shouldReturnZero() {
        // Given
        Instant createdAt = LocalDate.of(2023, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BigDecimal costPerDay = BigDecimal.ZERO;
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023, 1, 11); // 10 days

        // When
        BigDecimal result = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateTotalCost_negativeCostPerDay_shouldCalculateCorrectly() {
        // Given
        Instant createdAt = LocalDate.of(2023, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BigDecimal costPerDay = new BigDecimal("-5.00");
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023, 1, 6); // 5 days

        // When
        BigDecimal result = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(new BigDecimal("-25.00"), result);
    }

    @Test
    void calculateTotalCost_leapYear_shouldCalculateCorrectly() {
        // Given
        Instant createdAt = LocalDate.of(2020, 2, 28).atStartOfDay(ZoneId.systemDefault()).toInstant();
        BigDecimal costPerDay = new BigDecimal("10.00");
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2020, 3, 1); // 2 days (leap year)

        // When
        BigDecimal result = adminService.calculateTotalCost(createdAt, costPerDay, startDate, endDate);

        // Then
        assertEquals(new BigDecimal("20.00"), result);
    }

    /**
     * tests to function
     */
    @Test
    void toDto_minimalRequiredFields_shouldMapCorrectly() {
        // Given
        UserEntity editor = new UserEntity();
        editor.setId(1L);

        MagazineEntity magazineEntity = new MagazineEntity();
        magazineEntity.setId(1L);
        magazineEntity.setEditor(editor);
        magazineEntity.setTitle("Title");
        magazineEntity.setCostPerDay(null);

        // When
        MagazineAdminDto result = adminService.toDto(magazineEntity);

        // Then
        assertAll(
                () -> assertEquals(magazineEntity.getId(), result.id()),
                () -> assertEquals(magazineEntity.getTitle(), result.title()),
                () -> assertNull(result.costPerDay(), "Cost per day should be null when not set")
        );
    }

    @Test
    void toDto_shouldMaintainCostPrecision() {
        // Given
        UserEntity editor = new UserEntity();
        editor.setId(1L);

        BigDecimal testCost = new BigDecimal("10.00");

        MagazineEntity magazineEntity = new MagazineEntity();
        magazineEntity.setId(1L);
        magazineEntity.setEditor(editor);
        magazineEntity.setTitle("Title");
        magazineEntity.setCostPerDay(testCost);

        // When
        MagazineAdminDto result = adminService.toDto(magazineEntity);

        // Then
        assertEquals(testCost, result.costPerDay());
    }


}