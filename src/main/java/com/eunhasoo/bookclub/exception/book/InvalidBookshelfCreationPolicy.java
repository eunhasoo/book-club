package com.eunhasoo.bookclub.exception.book;

import com.eunhasoo.bookclub.book.domain.BookshelfCreationPolicy;
import com.eunhasoo.bookclub.exception.BookClubException;

public class InvalidBookshelfCreationPolicy extends BookClubException {

    private static final String INVALID_BOOKSHELF_CREATION_MESSAGE = "생성할 수 있는 책장 개수를 초과했습니다.";
    private static final String MAXIMUM = "최대 허용 범위 개수: ";

    public InvalidBookshelfCreationPolicy() {
        super(String.format("%s -> [%s: %d]", INVALID_BOOKSHELF_CREATION_MESSAGE, MAXIMUM, BookshelfCreationPolicy.MAXIMUM_SIZE_LIMIT));
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
