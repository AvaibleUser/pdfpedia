package org.cunoc.pdfpedia.repository.user;

import org.cunoc.pdfpedia.domain.entity.user.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findByUserId(Long userId);
}