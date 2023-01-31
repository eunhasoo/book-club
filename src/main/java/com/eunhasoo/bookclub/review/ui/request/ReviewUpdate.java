package com.eunhasoo.bookclub.review.ui.request;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class ReviewUpdate {

    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;

    @NotEmpty(message = "내용을 입력해주세요.")
    private String content;

    public ReviewUpdate(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
