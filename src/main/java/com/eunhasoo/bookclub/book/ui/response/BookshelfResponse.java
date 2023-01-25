package com.eunhasoo.bookclub.book.ui.response;

import com.eunhasoo.bookclub.book.domain.Bookshelf;
import lombok.Getter;

@Getter
public class BookshelfResponse {
    private Long id;
    private String name;

    public BookshelfResponse(Bookshelf bookshelf) {
        this.id = bookshelf.getId();
        this.name = bookshelf.getName();
    }
}
