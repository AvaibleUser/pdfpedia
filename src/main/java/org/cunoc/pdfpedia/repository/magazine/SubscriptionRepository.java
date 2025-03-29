package org.cunoc.pdfpedia.repository.magazine;

import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    Page<SubscriptionEntity> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    boolean existsByUserAndMagazineAndIsDeletedFalse(UserEntity user, MagazineEntity magazine);
}