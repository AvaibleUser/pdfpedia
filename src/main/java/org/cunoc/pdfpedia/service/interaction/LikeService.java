package org.cunoc.pdfpedia.service.interaction;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final MagazineRepository magazineRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveLike(Long magazineId, Long userId) {
        MagazineEntity magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new RuntimeException("Magazine not found"));

        if (magazine.isDisableLikes()) {
            throw new RuntimeException("Likes are disabled for this magazine");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        magazine.getLikes().add(user);

        magazineRepository.save(magazine);
    }

    @Transactional
    public void removeLike(Long magazineId, Long userId) {
        MagazineEntity magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new IllegalArgumentException("Magazine not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (!magazine.getLikes().contains(user)) {
            throw new IllegalStateException("User is not liked by magazine");
        }

        magazine.getLikes().remove(user);
        magazineRepository.save(magazine);
    }

    @Transactional
    public boolean hasUserLikedMagazine(Long magazineId, Long userId) {
        MagazineEntity magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new IllegalArgumentException("Magazine not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return magazine.getLikes().contains(user);
    }
}
