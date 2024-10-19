package com.team5.pyeonjip.comment.service;

import com.team5.pyeonjip.comment.entity.Comment;
import com.team5.pyeonjip.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByProductId(Long productId) {
        return commentRepository.findByProductId(productId);
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, Comment updatedComment) {
        return commentRepository.findById(id)
                .map(existingComment -> {
                    existingComment.setContent(updatedComment.getContent());
                    existingComment.setRating(updatedComment.getRating());
                    existingComment.setTitle(updatedComment.getTitle());
                    return commentRepository.save(existingComment);
                }).orElseThrow();
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
