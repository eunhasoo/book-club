package com.eunhasoo.bookclub.review.ui;

import com.eunhasoo.bookclub.auth.CurrentUser;
import com.eunhasoo.bookclub.review.application.CommentService;
import com.eunhasoo.bookclub.review.ui.request.CommentCreate;
import com.eunhasoo.bookclub.review.ui.request.CommentEdit;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@CurrentUser Long userId, @RequestBody @Valid CommentCreate commentCreate) {
        commentService.create(userId, commentCreate);
    }

    @PostMapping("/{commentId}")
    public void edit(@PathVariable Long commentId, @CurrentUser Long userId, @RequestBody @Valid CommentEdit commentEdit) {
        commentService.edit(commentId, userId, commentEdit);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId, @CurrentUser Long userId) {
        commentService.delete(commentId, userId);
    }
}
