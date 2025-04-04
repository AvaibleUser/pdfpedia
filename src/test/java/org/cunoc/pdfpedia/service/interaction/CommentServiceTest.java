package org.cunoc.pdfpedia.service.interaction;

import org.cunoc.pdfpedia.domain.dto.interaction.CommentDto;
import org.cunoc.pdfpedia.domain.entity.interaction.CommentEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.user.ProfileEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.repository.interaction.CommentRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MagazineRepository magazineRepository;

    @Mock
    private UserRepository userRepository;

    private MagazineEntity magazine;
    private UserEntity user;
    private CommentEntity comment;
    private ProfileEntity profile;

    @BeforeEach
    void setUp() {
        profile = new ProfileEntity();
        profile.setId(1L);
        profile.setFirstname("First Name");
        profile.setLastname("Last Name");
        magazine = new MagazineEntity();
        magazine.setId(1L);
        magazine.setTitle("Tech Magazine");

        user = new UserEntity();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@test.com");
        user.setProfile(profile);

        comment = CommentEntity.builder()
                .id(1L)
                .magazine(magazine)
                .user(user)
                .content("This is a test comment")
                .build();
    }

    @Test
    void givenValidData_whenSaveComment_thenReturnsCommentDto() {
        // Given
        Long magazineId = 1L;
        Long userId = 1L;
        String content = "This is a test comment";

        when(magazineRepository.findById(magazineId)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);

        // When
        CommentDto result = commentService.saveComment(magazineId, userId, content);

        // Then
        assertNotNull(result);
        assertEquals(comment.getId(), result.id());
        assertEquals(comment.getContent(), result.content());
        assertEquals(comment.getUser().getUsername(), result.user().username());

        verify(magazineRepository, times(1)).findById(magazineId);
        verify(userRepository, times(1)).findById(userId);
        verify(commentRepository, times(1)).save(any(CommentEntity.class));
    }

    @Test
    void givenNonExistentMagazine_whenSaveComment_thenThrowsException() {
        // Given
        Long magazineId = 99L;
        Long userId = 1L;
        String content = "This is a test comment";

        when(magazineRepository.findById(magazineId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                commentService.saveComment(magazineId, userId, content));

        assertEquals("Magazine not found", exception.getMessage());
        verify(magazineRepository, times(1)).findById(magazineId);
        verifyNoInteractions(userRepository, commentRepository);
    }

    @Test
    void givenNonExistentUser_whenSaveComment_thenThrowsException() {
        // Given
        Long magazineId = 1L;
        Long userId = 99L;
        String content = "This is a test comment";

        when(magazineRepository.findById(magazineId)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                commentService.saveComment(magazineId, userId, content));

        assertEquals("User not found", exception.getMessage());
        verify(magazineRepository, times(1)).findById(magazineId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void givenDatabaseError_whenSaveComment_thenThrowsException() {
        // Given
        Long magazineId = 1L;
        Long userId = 1L;
        String content = "This is a test comment";

        when(magazineRepository.findById(magazineId)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(CommentEntity.class))).thenThrow(DataIntegrityViolationException.class);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () ->
                commentService.saveComment(magazineId, userId, content));
    }

    /**
     * testes function getComment
     */
    @Test
    void givenMagazineId_whenGetCommentsByMagazine_thenReturnCommentDtoList() {
        // Given
        Long magazineId = 1L;
        when(commentRepository.findByMagazineId(magazineId)).thenReturn(List.of(comment));

        // When
        List<CommentDto> result = commentService.getCommentsByMagazine(magazineId);

        // Then
        assertThat(result.get(0).id()).isEqualTo(comment.getId());
        assertThat(result.get(0).content()).isEqualTo(comment.getContent());
        verify(commentRepository, times(1)).findByMagazineId(magazineId);
    }

    @Test
    void givenMagazineId_whenNoCommentsFound_thenReturnEmptyList() {
        // Given
        Long magazineId = 1L;
        when(commentRepository.findByMagazineId(magazineId)).thenReturn(Collections.emptyList());

        // When
        List<CommentDto> result = commentService.getCommentsByMagazine(magazineId);

        // Then
        assertThat(result.isEmpty()).isTrue();
        verify(commentRepository, times(1)).findByMagazineId(magazineId);
    }
}
