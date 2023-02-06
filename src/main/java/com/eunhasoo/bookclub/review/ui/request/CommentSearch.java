package com.eunhasoo.bookclub.review.ui.request;

import lombok.Getter;

@Getter
public class CommentSearch {

    private final int size = 20;
    private Integer page;

    public CommentSearch(Integer page) {
        this.page = page == null ? 1 : page;
    }

    public long getOffset() {
        return (long) Math.max(0, (page - 1)) * size;
    }
}
