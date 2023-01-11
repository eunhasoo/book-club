package com.eunhasoo.bookclub.helper;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Genre;

import java.time.LocalDate;

public class Fixture {

    public static BookInfo bookInfo() {
        return BookInfo.builder()
                .name("Harry Potter")
                .bookType(BookType.FICTION)
                .genre(Genre.FANTASY)
                .author("J.K. Rowling")
                .imageUrl("/img/harry_potter")
                .isbn("1234567891234")
                .publisher("Scholastic")
                .publishedDate(LocalDate.now())
                .build();
    }
}
