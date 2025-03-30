package org.cunoc.pdfpedia.controller.magazine;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.interaction.SubscriptionDto;
import org.cunoc.pdfpedia.service.magazine.SubscriptionService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/magazines/{magazineId}")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public SubscriptionDto subscribe(@PathVariable Long magazineId, @CurrentUserId long userId) {
        return subscriptionService.subscribe(userId, magazineId);
    }

    @DeleteMapping("/subscribe")
    public SubscriptionDto unsubscribe(@PathVariable Long magazineId, @CurrentUserId long userId) {
        return subscriptionService.unsubscribe(userId, magazineId);
    }

    @GetMapping("/subscribed")
    public Boolean isSubscribed(@PathVariable Long magazineId, @CurrentUserId long userId) {
        return subscriptionService.isSubscribed(userId, magazineId);
    }
}
