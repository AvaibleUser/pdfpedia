package org.cunoc.pdfpedia.service.admin;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.AdReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.EarningsReport;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineCostTotalDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earnings.MagazineReportDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.AdReportEmailDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.PaymentPostAdPerAnnouncerDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.earningsToAnnouncer.TotalReportPaymentPostAdByAnnouncersDto;
import org.cunoc.pdfpedia.domain.dto.admin.report.postAd.PostAdReportTotal;
import org.cunoc.pdfpedia.domain.dto.dashboard.CountRegisterByRolDto;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.type.PaymentType;
import org.cunoc.pdfpedia.repository.monetary.PaymentRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.cunoc.pdfpedia.service.monetary.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final AdminService adminService;
    private final PaymentRepository paymentRepository;

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

    public BigDecimal getTotalAdPostEmail(List<AdReportEmailDto> adPostReport) {
        return adPostReport.stream()
                .map(AdReportEmailDto::amount)
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

    public PostAdReportTotal getPostAdReportTotal(LocalDate startDate, LocalDate endDate, Integer type){
        List<AdReportDto> adPostReport = this.paymentService.getPaymentToPostAdByTypeAndBetween(startDate, endDate, type);
        BigDecimal totalAdPost = this.getTotalAdPost(adPostReport);

        return PostAdReportTotal
                .builder()
                .adReportDto(adPostReport)
                .totalAdPost(totalAdPost)
                .build();

    }

    public TotalReportPaymentPostAdByAnnouncersDto mapperReport(List<PaymentPostAdPerAnnouncerDto> payments,List<AdReportEmailDto> adReports ){
        // Mapeamos los anuncios a cada advertiser
        Map<String, List<AdReportEmailDto>> adReportsByUser = adReports.stream()
                .collect(Collectors.groupingBy(AdReportEmailDto::username));

        // Rellenamos la lista de ads en cada PaymentPostAdPerAnnouncerDto
        List<PaymentPostAdPerAnnouncerDto> enrichedPayments = payments.stream()
                .map(p -> p.toBuilder()
                        .adReportDtos(adReportsByUser.getOrDefault(p.username(), new ArrayList<>()))
                        .build()
                )
                .toList();

        BigDecimal totalAdPost = this.getTotalAdPostEmail(adReports);

        return new TotalReportPaymentPostAdByAnnouncersDto(enrichedPayments, totalAdPost);
    }

    @Transactional
    public TotalReportPaymentPostAdByAnnouncersDto getTotalPaymentsByAdvertisers() {
        List<PaymentPostAdPerAnnouncerDto> payments = paymentRepository.findGroupedPaymentsByPaymentType();
        List<AdReportEmailDto> adReports = paymentRepository.findAdReportsByPaymentType();

       return this.mapperReport(payments, adReports);
    }

    @Transactional
    public TotalReportPaymentPostAdByAnnouncersDto getTotalPaymentsByAdvertisers(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return getTotalPaymentsByAdvertisers();
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        List<PaymentPostAdPerAnnouncerDto> payments = paymentRepository.findGroupedPaymentsByPaymentTypeAndBetween(startInstant, endInstant);
        List<AdReportEmailDto> adReports = paymentRepository.findAdReportsByPaymentTypeAndBetween(startInstant, endInstant);

       return mapperReport(payments, adReports);

    }

    @Transactional
    public TotalReportPaymentPostAdByAnnouncersDto getTotalPaymentsByAdvertisersById(Long id) {
        List<PaymentPostAdPerAnnouncerDto> payments = paymentRepository.findGroupedPaymentsByPaymentTypeByIdUser(id);
        List<AdReportEmailDto> adReports = paymentRepository.findAdReportsByPaymentTypeByIdUser(id);

        return this.mapperReport(payments, adReports);
    }

    @Transactional
    public TotalReportPaymentPostAdByAnnouncersDto getTotalPaymentsByAdvertisersById(LocalDate startDate, LocalDate endDate, Long id) {
        if (startDate == null || endDate == null) {
            return getTotalPaymentsByAdvertisersById(id);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        List<PaymentPostAdPerAnnouncerDto> payments = paymentRepository.findGroupedPaymentsByPaymentTypeAndBetweenById(startInstant, endInstant, id);
        List<AdReportEmailDto> adReports = paymentRepository.findAdReportsByPaymentTypeAndBetweenById(startInstant, endInstant, id);

        return mapperReport(payments, adReports);

    }

}
