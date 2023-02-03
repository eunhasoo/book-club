package com.eunhasoo.bookclub.review.ui.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class CommentSearch {

    @NotNull
    private Long reviewId;

    private final int size = 20;
    private Integer page;

    public CommentSearch(Long reviewId, Integer page) {
        this.reviewId = reviewId;
        this.page = page == null ? 1 : page;
    }

    public long getOffset() {
        return (long) Math.max(0, (page - 1)) * size;
    }
}
