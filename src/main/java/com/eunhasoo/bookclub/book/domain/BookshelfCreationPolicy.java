package com.eunhasoo.bookclub.book.domain;

import com.eunhasoo.bookclub.exception.book.InvalidBookshelfCreationPolicy;
import org.springframework.stereotype.Component;

@Component
public class BookshelfCreationPolicy {

    public static final int MAXIMUM_SIZE_LIMIT = 15;

    public void validate(int size) {
        if (MAXIMUM_SIZE_LIMIT < size + 1) {
            throw new InvalidBookshelfCreationPolicy();
        }
    }
}
