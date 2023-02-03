package com.eunhasoo.bookclub.review.ui.request;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class CommentCreate {

    @NotNull
    private Long reviewId;

    @NotEmpty(message = "댓글 내용을 입력해주세요.")
    private String content;

    public CommentCreate(Long reviewId, String content) {
        this.reviewId = reviewId;
        this.content = content;
    }
}
