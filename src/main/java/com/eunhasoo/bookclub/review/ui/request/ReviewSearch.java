package com.eunhasoo.bookclub.review.ui.request;

import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Genre;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewSearch {

    private final int size = 5;

    private Integer page;
    private Genre genre;
    private BookType bookType;

    public ReviewSearch(Integer page, Genre genre, BookType bookType) {
        this.page = page == null ? 1 : page;
        this.genre = genre;
        this.bookType = bookType;
    }

    public long getOffset() {
        return (long) Math.max(0, (page - 1)) * size;
    }
}
