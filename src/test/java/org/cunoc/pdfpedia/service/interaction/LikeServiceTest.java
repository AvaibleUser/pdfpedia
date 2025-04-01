package org.cunoc.pdfpedia.service.interaction;

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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private MagazineRepository magazineRepository;

    @Mock
    private UserRepository userRepository;

    private MagazineEntity magazine;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");

        magazine = new MagazineEntity();
        magazine.setId(1L);
        magazine.setTitle("Test Magazine");
        magazine.setLikes(new HashSet<>());
    }

    @Test
    void shouldSaveLikeWhenMagazineAndUserExistAndLikesEnabled() {
        // Given
        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        likeService.saveLike(1L, 1L);

        // Then
        assertTrue(magazine.getLikes().contains(user));
        verify(magazineRepository, times(1)).save(magazine);
    }

    @Test
    void shouldThrowExceptionWhenMagazineNotFound() {
        // Given
        when(magazineRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> likeService.saveLike(1L, 1L));
        assertEquals("Magazine not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> likeService.saveLike(1L, 1L));
        assertEquals("User not found", exception.getMessage());
    }


    @Test
    void shouldNotDuplicateLikeIfAlreadyLiked() {
        // Given
        magazine.getLikes().add(user);
        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        likeService.saveLike(1L, 1L);

        // Then
        assertEquals(1, magazine.getLikes().size());
        verify(magazineRepository, times(1)).save(magazine);
    }

    /**
     * tests function removeLike
     */
    @Test
    void shouldRemoveLikeSuccessfully() {
        // Given
        magazine.getLikes().add(user);
        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        likeService.removeLike(1L, 1L);

        // Then
        assertFalse(magazine.getLikes().contains(user));
        verify(magazineRepository).save(magazine);
    }

    @Test
    void shouldThrowExceptionWhenMagazineNotFoundRemoveLike() {
        // Given
        when(magazineRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> likeService.removeLike(1L, 1L));
        assertEquals("Magazine not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundRemoveLike() {
        // Given
        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> likeService.removeLike(1L, 1L));
        assertEquals("user not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserDidNotLikeMagazine() {
        // Given
        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> likeService.removeLike(1L, 1L));
        assertEquals("User is not liked by magazine", exception.getMessage());
    }

    /**
     * test function hasUserLikedMagazine
     */
    @Test
    void shouldReturnTrue_WhenUserHasLikedMagazine() {
        // Given
        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        boolean result = likeService.hasUserLikedMagazine(1L, 1L);

        // Then
        assertFalse(result);
        verify(magazineRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldReturnFalse_WhenUserHasNotLikedMagazine() {
        // Given
        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotherUser");
        magazine.setLikes(Set.of(anotherUser));

        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        boolean result = likeService.hasUserLikedMagazine(1L, 1L);

        // Then
        assertFalse(result);
        verify(magazineRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowException_WhenMagazineNotFound() {
        // Given
        when(magazineRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                likeService.hasUserLikedMagazine(1L, 1L)
        );
        assertEquals("Magazine not found", exception.getMessage());
        verify(magazineRepository).findById(1L);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void shouldThrowException_WhenUserNotFound() {
        // Given
        when(magazineRepository.findById(1L)).thenReturn(Optional.of(magazine));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                likeService.hasUserLikedMagazine(1L, 1L)
        );
        assertEquals("User not found", exception.getMessage());
        verify(magazineRepository).findById(1L);
        verify(userRepository).findById(1L);
    }
}