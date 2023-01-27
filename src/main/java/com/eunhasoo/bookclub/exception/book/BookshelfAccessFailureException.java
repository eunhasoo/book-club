package com.eunhasoo.bookclub.exception.book;

import com.eunhasoo.bookclub.exception.BookClubException;

public class BookshelfAccessFailureException extends BookClubException {

    private static final String BOOKSHELF_ACCESS_FAIL_MESSAGE = "해당 책장은 비공개 상태이므로 접근할 수 없습니다.";

    public BookshelfAccessFailureException() {
        super(BOOKSHELF_ACCESS_FAIL_MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
