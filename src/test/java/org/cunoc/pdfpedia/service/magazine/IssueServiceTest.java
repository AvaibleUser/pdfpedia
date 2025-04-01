package org.cunoc.pdfpedia.service.magazine;

import static org.assertj.core.api.BDDAssertions.catchRuntimeException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.cunoc.pdfpedia.util.ThenMockAlias.thenMock;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;

import org.cunoc.pdfpedia.domain.dto.issue.IssueTitleDto;
import org.cunoc.pdfpedia.domain.dto.issue.MagazineIssueDto;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineIssueEntity;
import org.cunoc.pdfpedia.domain.exception.BadRequestException;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.magazine.IssueRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MagazineRepository magazineRepository;

    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueService issueService;

    @Test
    void canFindMagazineIssue() {
        // given
        long magazineId = 150L;
        long issueId = 100L;

        MagazineIssueDto expectedIssue = MagazineIssueDto.builder()
                .id(issueId)
                .title("amazing title")
                .pdfUrl("amazing url")
                .magazineId(magazineId)
                .magazineTitle("amazing title")
                .build();

        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(issueRepository.findByIdAndMagazineIdAndIsDeletedFalse(issueId, magazineId, MagazineIssueDto.class))
                .willReturn(Optional.of(expectedIssue));

        // when
        MagazineIssueDto actualIssue = issueService.findMagazineIssue(magazineId, issueId);

        // then
        then(actualIssue)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssue);
    }

    @Test
    void cantFindMagazineIssueIfUserDoesntExist() {
        // given
        long magazineId = 150L;
        long issueId = 100L;

        given(magazineRepository.existsById(magazineId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.findMagazineIssue(magazineId, issueId));

        // then
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantFindMagazineIssueIfMagazineDoesntExist() {
        // given
        long magazineId = 150L;
        long issueId = 100L;

        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(issueRepository.findByIdAndMagazineIdAndIsDeletedFalse(issueId, magazineId, MagazineIssueDto.class))
                .willReturn(Optional.empty());

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.findMagazineIssue(magazineId, issueId));

        // then
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(issueRepository).should().findByIdAndMagazineIdAndIsDeletedFalse(issueId, magazineId,
                MagazineIssueDto.class);
        then(actualException).isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void canFindMagazineIssues() {
        // given
        long magazineId = 150L;

        List<MagazineIssueDto> expectedIssues = List.of(
                MagazineIssueDto.builder()
                        .id(100L)
                        .title("amazing title")
                        .pdfUrl("amazing url")
                        .magazineId(magazineId)
                        .magazineTitle("amazing title")
                        .build(),
                MagazineIssueDto.builder()
                        .id(200L)
                        .title("amazing title 2")
                        .pdfUrl("amazing url 2")
                        .magazineId(magazineId)
                        .magazineTitle("amazing title")
                        .build());

        List<MagazineIssueDto> issues = expectedIssues.stream()
                .map(issue -> issue.toBuilder().build())
                .toList();

        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(issueRepository.findAllByMagazineIdAndIsDeletedFalse(magazineId, MagazineIssueDto.class))
                .willReturn(issues);

        // when
        List<MagazineIssueDto> actualIssues = issueService.findMagazineIssues(magazineId);

        // then
        then(actualIssues)
                .usingRecursiveComparison()
                .isEqualTo(issues);
    }

    @Test
    void cantFindMagazineIssuesIfUserDoesntExist() {
        // given
        long magazineId = 150L;

        given(magazineRepository.existsById(magazineId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.findMagazineIssues(magazineId));

        // then
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void canSaveMagazineIssue() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        long issueId = 100L;
        String pdfUrl = "amazing url";
        String magazineTitle = "amazing title";
        String issueTitle = "amazing title for issue";
        IssueTitleDto issueIn = new IssueTitleDto(issueTitle);
        MagazineEntity magazine = mock(MagazineEntity.class);

        MagazineIssueDto expectedIssue = MagazineIssueDto.builder()
                .id(issueId)
                .title(issueTitle)
                .pdfUrl(pdfUrl)
                .magazineId(magazineId)
                .magazineTitle(magazineTitle)
                .build();

        MagazineIssueEntity expectedIssueEntity = MagazineIssueEntity.builder()
                .pdfUrl(pdfUrl)
                .magazine(magazine)
                .title(issueTitle)
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(magazine));
        given(issueRepository.save(refEq(expectedIssueEntity))).willReturn(expectedIssueEntity.withId(issueId));

        given(magazine.getId()).willReturn(magazineId);
        given(magazine.getTitle()).willReturn(magazineTitle);

        // when
        MagazineIssueDto actualIssue = issueService.saveIssue(editorId, magazineId, pdfUrl, issueIn);

        // then
        thenMock(issueRepository).should().save(refEq(expectedIssueEntity));
        then(actualIssue)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssue);
    }

    @Test
    void cantSaveMagazineIssueIfUserDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        String pdfUrl = "amazing url";
        IssueTitleDto issueIn = new IssueTitleDto("amazing title for issue");

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.saveIssue(editorId, magazineId, pdfUrl, issueIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantSaveMagazineIssueIfMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        String pdfUrl = "amazing url";
        IssueTitleDto issueIn = new IssueTitleDto("amazing title for issue");

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.saveIssue(editorId, magazineId, pdfUrl, issueIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantSaveMagazineIssueIfEditorMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        String pdfUrl = "amazing url";
        IssueTitleDto issueIn = new IssueTitleDto("amazing title for issue");

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.empty());

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.saveIssue(editorId, magazineId, pdfUrl, issueIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(magazineRepository).should().findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId,
                MagazineEntity.class);
        then(actualException).isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void canUpdateMagazineIssueIfThereAreNoChanges() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        long issueId = 100L;
        IssueTitleDto issueIn = new IssueTitleDto(Optional.empty());

        MagazineIssueDto expectedIssue = MagazineIssueDto.builder()
                .id(issueId)
                .pdfUrl("amazing url")
                .magazineId(magazineId)
                .magazineTitle("amazing title")
                .build();

        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(issueRepository.findByIdAndMagazineIdAndIsDeletedFalse(issueId, magazineId, MagazineIssueDto.class))
                .willReturn(Optional.of(expectedIssue));

        // when
        MagazineIssueDto actualIssue = issueService.updateIssue(editorId, magazineId, issueId, issueIn);

        // then
        then(actualIssue)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssue);
    }

    @Test
    void canUpdateMagazineIssueIfThereAreChanges() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        long issueId = 100L;
        String title = "amazing title for issue";
        String pdfUrl = "amazing url";
        String magazineTitle = "amazing title";
        MagazineEntity magazine = mock(MagazineEntity.class);

        IssueTitleDto issueIn = new IssueTitleDto(title);
        MagazineIssueDto expectedIssue = MagazineIssueDto.builder()
                .id(issueId)
                .title(title)
                .pdfUrl(pdfUrl)
                .magazineId(magazineId)
                .magazineTitle(magazineTitle)
                .build();

        MagazineIssueEntity expectedIssueEntity = MagazineIssueEntity.builder()
                .id(issueId)
                .pdfUrl(pdfUrl)
                .magazine(magazine)
                .title(title)
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(issueRepository.findByIdAndMagazineIdAndMagazineEditorIdAndIsDeletedFalse(issueId, magazineId, editorId,
                MagazineIssueEntity.class)).willReturn(Optional.of(expectedIssueEntity.withTitle(null)));

        given(magazine.getId()).willReturn(magazineId);
        given(magazine.getTitle()).willReturn(magazineTitle);

        // when
        MagazineIssueDto actualIssue = issueService.updateIssue(editorId, magazineId, issueId, issueIn);

        // then
        thenMock(issueRepository).should().save(refEq(expectedIssueEntity));
        then(actualIssue)
                .usingRecursiveComparison()
                .isEqualTo(expectedIssue);
    }

    @Test
    void cantUpdateMagazineIssueIfUserDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        long issueId = 100L;
        IssueTitleDto issueIn = new IssueTitleDto("amazing title for issue");

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.updateIssue(editorId, magazineId, issueId, issueIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantUpdateMagazineIssueIfMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        long issueId = 100L;
        IssueTitleDto issueIn = new IssueTitleDto("amazing title for issue");

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.updateIssue(editorId, magazineId, issueId, issueIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantUpdateMagazineIssueIfIssueDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        long issueId = 100L;
        IssueTitleDto issueIn = new IssueTitleDto("amazing title for issue");

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(issueRepository.findByIdAndMagazineIdAndMagazineEditorIdAndIsDeletedFalse(issueId, magazineId, editorId,
                MagazineIssueEntity.class)).willReturn(Optional.empty());

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.updateIssue(editorId, magazineId, issueId, issueIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(issueRepository).should().findByIdAndMagazineIdAndMagazineEditorIdAndIsDeletedFalse(issueId,
                magazineId, editorId, MagazineIssueEntity.class);
        then(actualException).isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void canDeleteMagazineIssue() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        long issueId = 100L;

        MagazineIssueEntity expectedIssue = MagazineIssueEntity.builder()
                .id(issueId)
                .pdfUrl("amazing url")
                .magazine(mock(MagazineEntity.class))
                .isDeleted(true)
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(issueRepository.findByIdAndMagazineIdAndMagazineEditorIdAndIsDeletedFalse(issueId, magazineId, editorId,
                MagazineIssueEntity.class)).willReturn(Optional.of(expectedIssue.withDeleted(false)));

        // when
        issueService.deleteIssue(editorId, magazineId, issueId);

        // then
        thenMock(issueRepository).should().save(refEq(expectedIssue));
    }

    @Test
    void cantDeleteMagazineIssueIfUserDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        long issueId = 100L;

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.deleteIssue(editorId, magazineId, issueId));

        // then
        thenMock(userRepository).should().existsById(editorId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantDeleteMagazineIssueIfMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        long issueId = 100L;

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> issueService.deleteIssue(editorId, magazineId, issueId));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }
}
