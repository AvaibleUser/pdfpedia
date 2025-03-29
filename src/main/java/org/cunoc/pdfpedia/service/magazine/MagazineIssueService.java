package org.cunoc.pdfpedia.service.magazine;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineIssueDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.repository.magazine.MagazineIssueRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.magazine.SubscriptionRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MagazineIssueService {

    private final MagazineRepository magazineRepository;

    @Transactional(readOnly = true)
    public Set<MagazineIssueDto> getMagazineIssues(Long magazineId) {
        MagazineEntity magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new IllegalArgumentException("Magazine not found with id: " + magazineId));

        return magazine.getIssues().stream()
                .map(MagazineIssueDto::fromEntity)
                .collect(Collectors.toSet());
    }

}
