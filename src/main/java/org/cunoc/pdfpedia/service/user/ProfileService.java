package org.cunoc.pdfpedia.service.user;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.user.ProfileDto;
import org.cunoc.pdfpedia.domain.entity.user.ProfileEntity;
import org.cunoc.pdfpedia.repository.user.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ProfileDto getProfileByUserId(Long userId) {
        var profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        return ProfileDto.fromEntity(profile);
    }

    @Transactional
    public ProfileDto updateProfile(ProfileDto profileDto) {
        ProfileEntity existingProfile = profileRepository.findById(profileDto.id())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        existingProfile.setHobbies(profileDto.hobbies());
        existingProfile.setDescription(profileDto.description());
        existingProfile.setInterestsTopics(profileDto.interestsTopics());

        return ProfileDto.fromEntity(profileRepository.save(existingProfile));
    }
}
