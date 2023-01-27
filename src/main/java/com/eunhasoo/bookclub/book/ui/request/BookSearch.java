package com.eunhasoo.bookclub.book.ui.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class BookSearch {

    @NotNull
    private Long bookshelfId;

    @Builder.Default
    private int page = 1;

    private final int size = 15;

    public BookSearch(Long bookshelfId, int page) {
        this.bookshelfId = bookshelfId;
        this.page = page;
    }

    public long getOffset() {
        return Math.max(0, (page - 1)) * size;
    }
}
