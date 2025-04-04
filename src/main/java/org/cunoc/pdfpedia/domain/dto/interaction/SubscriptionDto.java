package org.cunoc.pdfpedia.domain.dto.interaction;

import org.cunoc.pdfpedia.domain.entity.interaction.SubscriptionEntity;

public record SubscriptionDto(
        Long idMagazine,
        Long idUser
) {
    public static SubscriptionDto fromEntity(SubscriptionEntity subscription) {
        return new SubscriptionDto(
                subscription.getId().getMagazineId(), subscription.getId().getUserId()
        );
    }
}
