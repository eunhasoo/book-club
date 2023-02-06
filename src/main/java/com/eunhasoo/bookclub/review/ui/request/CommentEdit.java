package com.eunhasoo.bookclub.review.ui.request;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class CommentEdit {

    @NotEmpty(message = "댓글 내용을 입력해주세요.")
    private String content;

    private CommentEdit() {}

    public CommentEdit(String content) {
        this.content = content;
    }
}
