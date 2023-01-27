package com.eunhasoo.bookclub.book.ui.response;

import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.ReadProcess;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookResponse {
    private Long id;
    private Long bookInfoId;
    private ReadProcess readProcess;

    public BookResponse(Book book) {
        this.id = book.getId();
        this.bookInfoId = book.getBookInfo().getId();
        this.readProcess = book.getReadProcess();
    }
}
