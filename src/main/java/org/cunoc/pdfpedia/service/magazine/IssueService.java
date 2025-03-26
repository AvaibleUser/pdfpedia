package org.cunoc.pdfpedia.service.magazine;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.cunoc.pdfpedia.domain.dto.issue.IssueTitleDto;
import org.cunoc.pdfpedia.domain.dto.issue.MagazineIssueDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineIssueEntity;
import org.cunoc.pdfpedia.domain.exception.BadRequestException;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.magazine.IssueRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueService implements IIssueService {

    private final UserRepository userRepository;
    private final MagazineRepository magazineRepository;
    private final IssueRepository issueRepository;

    @Override
    public MagazineIssueDto findMagazineIssue(long magazineId, long issueId) {
        if (!magazineRepository.existsById(magazineId)) {
            throw new BadRequestException("La revista no existe");
        }
        return issueRepository.findByIdAndMagazineId(issueId, magazineId, MagazineIssueDto.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontró la revista"));
    }

    @Override
    public List<MagazineIssueDto> findMagazineIssues(long magazineId) {
        if (!magazineRepository.existsById(magazineId)) {
            throw new BadRequestException("La revista no existe");
        }
        return issueRepository.findAllByMagazineIdAndIsDeletedFalse(magazineId, MagazineIssueDto.class);
    }

    @Override
    @Transactional
    public MagazineIssueDto saveIssue(long editorId, long magazineId, String pdfUrl, IssueTitleDto newIssue) {
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        if (!magazineRepository.existsById(magazineId)) {
            throw new BadRequestException("La revista no existe");
        }
        MagazineEntity magazine = magazineRepository.findById(magazineId).get();

        MagazineIssueEntity issue = issueRepository.save(MagazineIssueEntity.builder()
                .magazine(magazine)
                .title(newIssue.title().filter(StringUtils::isBlank).orElse(null))
                .pdfUrl(pdfUrl)
                .build());

        return MagazineIssueDto.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .pdfUrl(issue.getPdfUrl())
                .magazineId(issue.getMagazine().getId())
                .magazineTitle(issue.getMagazine().getTitle())
                .build();
    }

    @Override
    @Transactional
    public MagazineIssueDto updateIssue(long editorId, long magazineId, long issueId, IssueTitleDto newIssue) {
        Optional<String> title = newIssue.title().filter(StringUtils::isNotBlank);
        if (title.isEmpty()) {
            return findMagazineIssue(magazineId, issueId);
        }
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        if (!magazineRepository.existsById(magazineId)) {
            throw new BadRequestException("La revista no existe");
        }

        MagazineIssueEntity issue = issueRepository
                .findByIdAndMagazineIdAndMagazineEditorIdAndIsDeletedFalse(issueId, magazineId, editorId,
                        MagazineIssueEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontró el numero de la revista"));

        title.ifPresent(issue::setTitle);
        issueRepository.save(issue);

        return MagazineIssueDto.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .pdfUrl(issue.getPdfUrl())
                .magazineId(issue.getMagazine().getId())
                .magazineTitle(issue.getMagazine().getTitle())
                .build();
    }

    @Override
    public void deleteIssue(long editorId, long magazineId, long issueId) {
        if (!userRepository.existsById(editorId)) {
            throw new BadRequestException("El usuario no existe");
        }
        if (!magazineRepository.existsById(magazineId)) {
            throw new BadRequestException("La revista no existe");
        }
        issueRepository
                .findByIdAndMagazineIdAndMagazineEditorIdAndIsDeletedFalse(issueId, magazineId, editorId,
                        MagazineIssueEntity.class)
                .ifPresent(i -> {
                    i.setDeleted(true);
                    issueRepository.save(i);
                });
    }
}
