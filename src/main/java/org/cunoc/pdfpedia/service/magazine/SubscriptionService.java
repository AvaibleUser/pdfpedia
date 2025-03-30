package org.cunoc.pdfpedia.service.magazine;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.interaction.SubscriptionDto;
import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionEntity;
import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionId;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.repository.interaction.SubscriptionRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MagazineRepository magazineRepository;

    @Transactional
    public SubscriptionDto subscribe(Long idUser, Long magazineId) {
        UserEntity user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found"));
        MagazineEntity magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new RuntimeException("Magazine not found"));

        SubscriptionId id = new SubscriptionId(user.getId(), magazine.getId());
        if (subscriptionRepository.existsById(id)) {
            throw new RuntimeException("User has already subscribed");
        }

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .id(id)
                .user(user)
                .magazine(magazine)
                .subscribedAt(Instant.now())
                .isDeleted(false)
                .build();


        return SubscriptionDto.fromEntity(subscriptionRepository.save(subscription));
    }

    @Transactional
    public SubscriptionDto unsubscribe(Long idUser, Long magazineId) {
        UserEntity user = userRepository.findById(idUser)

                .orElseThrow(() -> new RuntimeException("User not found"));
        SubscriptionId id = new SubscriptionId(user.getId(), magazineId);
        SubscriptionEntity subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscriptionRepository.delete(subscription);
        return SubscriptionDto.fromEntity(subscription);
    }

    @Transactional(readOnly = true)
    public boolean isSubscribed(Long idUser, Long magazineId) {
        UserEntity user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found"));
        SubscriptionId id = new SubscriptionId(user.getId(), magazineId);
        return subscriptionRepository.existsById(id);
    }

}
