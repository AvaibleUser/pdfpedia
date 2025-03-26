package org.cunoc.pdfpedia.controller.interaction;

import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.interaction.CommentDto;
import org.cunoc.pdfpedia.service.interaction.CommentService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/magazines/{id}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto saveComment(@PathVariable Long id, @CurrentUserId long userId, @RequestBody CommentDto comment) {
        return commentService.saveComment(id, userId, comment.content());
    }

    @GetMapping
    public List<CommentDto> getComments(@PathVariable Long id) {
        return commentService.getCommentsByMagazine(id);
    }
}
