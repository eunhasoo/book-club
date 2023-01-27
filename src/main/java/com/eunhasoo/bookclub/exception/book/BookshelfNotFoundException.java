package com.eunhasoo.bookclub.exception.book;

import com.eunhasoo.bookclub.exception.BookClubException;

public class BookshelfNotFoundException extends BookClubException {

    private static final String BOOKSHELF_NOT_FOUND = "책장을 찾을 수 없습니다.";

    public BookshelfNotFoundException(Long bookShelfId) {
        super(String.format("%s -> [bookShelfId: %d]", bookShelfId));
    }

    public BookshelfNotFoundException(Long bookShelfId, Long userId) {
        super(String.format("%s -> [bookShelfId: %d, userId: %d]", BOOKSHELF_NOT_FOUND, bookShelfId, userId));
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
