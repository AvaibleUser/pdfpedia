package org.cunoc.pdfpedia.service.interaction;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.interaction.CommentDto;
import org.cunoc.pdfpedia.domain.entity.interaction.CommentEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.repository.interaction.CommentRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MagazineRepository magazineRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDto saveComment(Long magazineId, Long userId, String content) {
        MagazineEntity magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new IllegalArgumentException("Magazine not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CommentEntity comment = CommentEntity.builder()
                .magazine(magazine)
                .user(user)
                .content(content)
                .build();

        return CommentDto.fromEntity(commentRepository.save(comment));
    }

    @Transactional
    public List<CommentDto> getCommentsByMagazine(Long magazineId) {
        List<CommentEntity> comments = commentRepository.findByMagazineId(magazineId);
        return comments.stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }
}
