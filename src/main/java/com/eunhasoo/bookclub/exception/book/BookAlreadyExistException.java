package com.eunhasoo.bookclub.exception.book;

import com.eunhasoo.bookclub.exception.BookClubException;

public class BookAlreadyExistException extends BookClubException {

    private static final String BOOK_ALREADY_EXIST_MESSAGE = "해당 책이 동일한 책장에 이미 존재합니다.";

    public BookAlreadyExistException() {
        super(BOOK_ALREADY_EXIST_MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
