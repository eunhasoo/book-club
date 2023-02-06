package com.eunhasoo.bookclub.book.ui.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class BookSearch {

    @Builder.Default
    private int page = 1;

    private final int size = 15;

    public BookSearch(int page) {
        this.page = page;
    }

    public long getOffset() {
        return Math.max(0, (page - 1)) * size;
    }
}
