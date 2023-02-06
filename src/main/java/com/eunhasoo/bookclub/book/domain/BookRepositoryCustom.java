package com.eunhasoo.bookclub.book.domain;

import com.eunhasoo.bookclub.book.ui.request.BookSearch;

import java.util.List;

public interface BookRepositoryCustom {

    List<Book> getList(Long bookshelfId, BookSearch bookSearch);
}
