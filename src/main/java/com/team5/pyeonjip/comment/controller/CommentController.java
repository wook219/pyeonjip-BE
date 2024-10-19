package com.team5.pyeonjip.comment.controller;

import com.team5.pyeonjip.comment.entity.Comment;
import com.team5.pyeonjip.comment.repository.CommentRepository;
import com.team5.pyeonjip.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    //private final CommentRepository commentRepository;
    private final CommentService commentService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getCommentsByProductId(@PathVariable("productId") Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.saveComment(comment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long id,
            @RequestBody Comment updatedComment) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateComment(id, updatedComment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
