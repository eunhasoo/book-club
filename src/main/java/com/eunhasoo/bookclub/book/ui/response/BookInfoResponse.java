package com.eunhasoo.bookclub.book.ui.response;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Genre;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookInfoResponse {
    private Long id;
    private String isbn;
    private String name;
    private String author;
    private String publisher;
    private String imageUrl;
    private LocalDate publishedDate;
    private BookType bookType;
    private Genre genre;

    public BookInfoResponse(BookInfo bookInfo) {
        this.id = bookInfo.getId();
        this.isbn = bookInfo.getIsbn();
        this.name = bookInfo.getName();
        this.author = bookInfo.getAuthor();
        this.publisher = bookInfo.getPublisher();
        this.publishedDate = bookInfo.getPublishedDate();
        this.imageUrl = bookInfo.getImageUrl();
        this.bookType = bookInfo.getBookType();
        this.genre = bookInfo.getGenre();
    }
}
