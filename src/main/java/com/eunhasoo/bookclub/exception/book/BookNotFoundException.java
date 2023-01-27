package com.eunhasoo.bookclub.exception.book;

import com.eunhasoo.bookclub.exception.BookClubException;

public class BookNotFoundException extends BookClubException {

    private static final String BOOK_NOT_FOUND_MESSAGE = "책을 찾을 수 없습니다.";

    public BookNotFoundException(Long bookId, Long userId) {
        super(String.format("%s -> [bookId: %d, userId: %d]", BOOK_NOT_FOUND_MESSAGE, bookId, userId));
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
