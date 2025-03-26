package org.cunoc.pdfpedia.service.admin;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.EarningsReport;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineCostTotalDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineReportDto;
import org.cunoc.pdfpedia.domain.dto.dashboard.CountRegisterByRolDto;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.type.PaymentType;
import org.cunoc.pdfpedia.repository.monetary.PaymentRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.cunoc.pdfpedia.service.monetary.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final AdminService adminService;

    public List<CountRegisterByRolDto> findCountRegisterByRol(LocalDate startDate, LocalDate endDate) {
        List<CountRegisterByRolDto> countRegisterByRol = new ArrayList<>();

        if (startDate == null || endDate == null) {
            countRegisterByRol.add(CountRegisterByRolDto
                    .builder()
                    .count(this.userRepository.countByRole_Name("ANNOUNCER"))
                    .typeUser("ANNOUNCER")
                    .build());
            countRegisterByRol.add(CountRegisterByRolDto
                    .builder()
                    .count(this.userRepository.countByRole_Name("EDITOR"))
                    .typeUser("EDITOR")
                    .build());
            countRegisterByRol.add(CountRegisterByRolDto
                    .builder()
                    .count(this.userRepository.countByRole_Name("USER"))
                    .typeUser("USER")
                    .build());

            return countRegisterByRol;
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        countRegisterByRol.add(CountRegisterByRolDto
                .builder().
                count(this.userRepository.countByRole_NameAndCreatedAtBetween("ANNOUNCER", startInstant, endInstant))
                .typeUser("ANNOUNCER")
                .build());
        countRegisterByRol.add(CountRegisterByRolDto
                .builder()
                .count(this.userRepository.countByRole_NameAndCreatedAtBetween("EDITOR", startInstant, endInstant))
                .typeUser("EDITOR")
                .build());
        countRegisterByRol.add(CountRegisterByRolDto
                .builder()
                .count(this.userRepository.countByRole_NameAndCreatedAtBetween("USER", startInstant, endInstant))
                .typeUser("USER")
                .build());

        return countRegisterByRol;
    }

    public BigDecimal getTotalAdPost(List<AdReportDto> adPostReport) {
        return adPostReport.stream()
                .map(AdReportDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalBlockAd(List<MagazineReportDto> blockAdReport) {
        return blockAdReport.stream()
                .map(MagazineReportDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getCostTotal(List<MagazineCostTotalDto> costTotalReport) {
        return costTotalReport.stream()
                .map(MagazineCostTotalDto::costTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public EarningsReport getTotalReportEarnings(LocalDate startDate, LocalDate endDate){
        List<AdReportDto> adPostReport = this.paymentService.getPaymentToPostAdBetween(startDate, endDate);
        List<MagazineReportDto> blockAdReport = this.paymentService.getPaymentToBlockAdMagazineBetween(startDate, endDate);
        List<MagazineCostTotalDto> costTotalReport = this.adminService.getAllCostTotalMagazines(startDate, endDate);

        BigDecimal totalAdPost = this.getTotalAdPost(adPostReport);
        BigDecimal totalBlockAd = this.getTotalBlockAd(blockAdReport);
        BigDecimal totalCostTotal = this.getCostTotal(costTotalReport);
        BigDecimal totalEarnings = totalAdPost.add(totalBlockAd).subtract(totalCostTotal);

        return EarningsReport
                .builder()
                .adReportDto(adPostReport)
                .magazineReportDto(blockAdReport)
                .magazineCostTotalDto(costTotalReport)
                .totalAdPost(totalAdPost)
                .totalAdBlocks(totalBlockAd)
                .totalCostPerDay(totalCostTotal)
                .totalIncome(totalAdPost.add(totalBlockAd))
                .totalEarnings(totalEarnings)
                .build();
    }

}
