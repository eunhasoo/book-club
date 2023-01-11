package com.eunhasoo.bookclub.exception.book;

import com.eunhasoo.bookclub.exception.BookClubException;

public class BookInfoNotFoundException extends BookClubException {

    private static final String BOOK_INFO_NOT_FOUND = "책 정보를 찾을 수 없습니다.";

    public BookInfoNotFoundException(Long bookInfoId) {
        super(String.format("%s -> [bookInfoId: %d]", BOOK_INFO_NOT_FOUND, bookInfoId));
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
