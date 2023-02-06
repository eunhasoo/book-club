package com.eunhasoo.bookclub.review.ui;

import com.eunhasoo.bookclub.auth.CurrentUser;
import com.eunhasoo.bookclub.common.ResultList;
import com.eunhasoo.bookclub.review.application.CommentService;
import com.eunhasoo.bookclub.review.domain.Comment;
import com.eunhasoo.bookclub.review.ui.request.CommentCreate;
import com.eunhasoo.bookclub.review.ui.request.CommentSearch;
import com.eunhasoo.bookclub.review.ui.response.CommentListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResultList<?> getComments(CommentSearch commentSearch) {
        List<CommentListResponse> result = commentService.getComments(commentSearch)
                .stream()
                .map(CommentListResponse::new)
                .collect(Collectors.toUnmodifiableList());

        return new ResultList(result.size(), result);
    }

    @PostMapping
    public void create(@CurrentUser Long userId, @RequestBody @Valid CommentCreate commentCreate) {
        commentService.create(userId, commentCreate);
    }
}