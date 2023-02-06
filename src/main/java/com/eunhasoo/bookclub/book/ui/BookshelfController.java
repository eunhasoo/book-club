package com.eunhasoo.bookclub.book.ui;

import com.eunhasoo.bookclub.auth.CurrentUser;
import com.eunhasoo.bookclub.book.application.BookService;
import com.eunhasoo.bookclub.book.application.BookshelfService;
import com.eunhasoo.bookclub.book.ui.request.BookSearch;
import com.eunhasoo.bookclub.book.ui.request.BookshelfCreate;
import com.eunhasoo.bookclub.book.ui.request.BookshelfUpdate;
import com.eunhasoo.bookclub.book.ui.response.BookListResponse;
import com.eunhasoo.bookclub.book.ui.response.BookshelfResponse;
import com.eunhasoo.bookclub.common.ResultList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/bookshelves")
@RestController
public class BookshelfController {

    private final BookshelfService bookshelfService;
    private final BookService bookService;

    public BookshelfController(BookshelfService bookshelfService, BookService bookService) {
        this.bookshelfService = bookshelfService;
        this.bookService = bookService;
    }

    @GetMapping
    public ResultList<?> getAll(@CurrentUser Long userId) {
        List<BookshelfResponse> bookShelves = bookshelfService.getAll(userId)
                .stream()
                .map(BookshelfResponse::new)
                .collect(Collectors.toUnmodifiableList());

        return new ResultList(bookShelves.size(), bookShelves);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@CurrentUser Long userId, @RequestBody @Valid BookshelfCreate bookShelfCreate) {
        bookshelfService.create(userId, bookShelfCreate);
    }

    @PostMapping("/{bookshelfId}")
    public void update(@PathVariable Long bookshelfId,
                       @CurrentUser Long userId,
                       @RequestBody @Valid BookshelfUpdate bookshelfUpdate) {
        bookshelfService.update(bookshelfId, userId, bookshelfUpdate);
    }

    @DeleteMapping("/{bookshelfId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@CurrentUser Long userId, @PathVariable Long bookshelfId) {
        bookshelfService.delete(bookshelfId, userId);
    }

    @GetMapping("/{bookshelfId}/books")
    public ResultList<?> getAll(@CurrentUser Long userId,
                                @PathVariable Long bookshelfId,
                                BookSearch bookSearch) {
        List<BookListResponse> books = bookService.getAll(userId, bookshelfId, bookSearch)
                .stream()
                .map(BookListResponse::new)
                .collect(Collectors.toUnmodifiableList());

        return new ResultList(books.size(), books);
    }
}
