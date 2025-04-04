package org.cunoc.pdfpedia.controller.user;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.user.ProfileDto;
import org.cunoc.pdfpedia.service.user.ProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user/{id}/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ProfileDto getProfile(@PathVariable long id) {
        return this.profileService.getProfileByUserId(id);
    }

    @PutMapping
    public ProfileDto updateProfile(@RequestBody ProfileDto profileDto) {
        return this.profileService.updateProfile(profileDto);
    }

}
