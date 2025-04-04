package org.cunoc.pdfpedia.controller.interaction;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.service.interaction.LikeService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/magazines/{id}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public void saveLike(@PathVariable Long id, @CurrentUserId long userId) {
        likeService.saveLike(id, userId);
    }

    @DeleteMapping
    public void removeLike(@PathVariable Long id, @CurrentUserId long userId) {
        likeService.removeLike(id, userId);
    }

    @GetMapping("/check")
    public boolean hasUserLikedMagazine(@PathVariable Long id, @CurrentUserId long userId) {
        return likeService.hasUserLikedMagazine(id, userId);
    }
}
