package com.team5.pyeonjip.comment.service;

import com.team5.pyeonjip.comment.entity.Comment;
import com.team5.pyeonjip.comment.repository.CommentRepository;
import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import com.team5.pyeonjip.global.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByProductId(Long productId) {
        // 빈값도 허용을 해줘야 하기 때문에 예외처리 하면 안됨
        return commentRepository.findByProductId(productId);
    }

    @Transactional
    public Comment saveComment(Comment comment) {
        validateCommentContent(comment);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long id, Comment updatedComment) {
        validateCommentContent(updatedComment);
        return commentRepository.findById(id)
                .map(existingComment -> updateExistingComment(existingComment, updatedComment))
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
    }

    private void validateCommentContent(Comment comment) {
        if (comment.getContent() == null || comment.getContent().isBlank()) {
            throw new GlobalException(ErrorCode.EMPTY_COMMENT_CONTENT);
        }
        if (comment.getTitle() == null || comment.getTitle().isBlank()) {
            throw new GlobalException(ErrorCode.EMPTY_COMMENT_TITLE);
        }
        if (comment.getRating() == null) {
            throw new GlobalException(ErrorCode.EMPTY_COMMENT_RATING);
        }
    }

    private Comment updateExistingComment(Comment existing, Comment updated) {
        existing.setContent(updated.getContent());
        existing.setRating(updated.getRating());
        existing.setTitle(updated.getTitle());
        return existing;
    }

}
