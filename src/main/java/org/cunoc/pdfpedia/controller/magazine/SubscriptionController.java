package org.cunoc.pdfpedia.controller.magazine;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineSubscriptionDto;
import org.cunoc.pdfpedia.service.magazine.SubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/magazines")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/subscription/{userId}")
    public Page<MagazineSubscriptionDto> getUserSubscriptions(@PathVariable Long userId, Pageable pageable) {
        return subscriptionService.getUserSubscriptions(userId, pageable);
    }

    @PostMapping("/{magazineId}/subscription/{userId}")
    public void subscribeUserToMagazine(@PathVariable Long magazineId, @PathVariable Long userId) {
        subscriptionService.subscribeUserToMagazine(userId, magazineId);
    }
}
