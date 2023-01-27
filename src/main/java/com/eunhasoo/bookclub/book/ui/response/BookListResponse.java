package com.eunhasoo.bookclub.book.ui.response;

import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import lombok.Getter;

@Getter
public class BookListResponse {
    private Long id;
    private Long bookInfoId;
    private String name;
    private String author;
    private String imageUrl;

    public BookListResponse(Book book) {
        this.id = book.getId();

        BookInfo bookInfo = book.getBookInfo();
        this.bookInfoId = bookInfo.getId();
        this.name = bookInfo.getName();
        this.author = bookInfo.getAuthor();
        this.imageUrl = bookInfo.getImageUrl();
    }
}
