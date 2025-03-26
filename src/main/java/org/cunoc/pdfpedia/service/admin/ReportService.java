package org.cunoc.pdfpedia.service.admin;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.dashboard.CountRegisterByRolDto;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;

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
}
