package com.eunhasoo.bookclub.book.ui.response;

import lombok.Getter;

@Getter
public class BookInfoCreateResponse {
    private Long bookInfoId;

    public BookInfoCreateResponse(Long bookInfoId) {
        this.bookInfoId = bookInfoId;
    }
}
