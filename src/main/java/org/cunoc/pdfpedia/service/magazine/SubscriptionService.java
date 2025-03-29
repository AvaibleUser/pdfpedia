package org.cunoc.pdfpedia.service.magazine;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineSubscriptionDto;
import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionEntity;
import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionId;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.repository.magazine.MagazineIssueRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.magazine.SubscriptionRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final MagazineRepository magazineRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<MagazineSubscriptionDto> getUserSubscriptions(Long userId, Pageable pageable) {
        Page<SubscriptionEntity> subscriptions = subscriptionRepository.findByUserIdAndIsDeletedFalse(userId, pageable);

        return subscriptions.map(sub -> MagazineSubscriptionDto.fromEntity(sub.getId().getMagazine()));

    }

    @Transactional
    public void subscribeUserToMagazine(Long userId, Long magazineId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        MagazineEntity magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new IllegalArgumentException("Magazine not found with id: " + magazineId));

        if (subscriptionRepository.existsByUserAndMagazineAndIsDeletedFalse(user, magazine)) {
            throw new IllegalStateException("User is already subscribed to this magazine.");
        }

        SubscriptionId subscriptionId = new SubscriptionId(user, magazine);

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .id(subscriptionId)
                .subscribedAt(Instant.now())
                .isDeleted(false)
                .build();
        subscriptionRepository.save(subscription);
    }
}
