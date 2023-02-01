package com.eunhasoo.bookclub.book.ui;

import com.eunhasoo.bookclub.auth.CurrentUser;
import com.eunhasoo.bookclub.book.application.BookService;
import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.ui.request.BookCreate;
import com.eunhasoo.bookclub.book.ui.request.BookSearch;
import com.eunhasoo.bookclub.book.ui.request.BookUpdate;
import com.eunhasoo.bookclub.book.ui.response.BookListResponse;
import com.eunhasoo.bookclub.book.ui.response.BookResponse;
import com.eunhasoo.bookclub.common.ResultList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/books")
@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResultList<?> getAll(@CurrentUser Long userId,
                                @ModelAttribute BookSearch bookSearch) {
        List<BookListResponse> books = bookService.getAll(userId, bookSearch)
                .stream()
                .map(BookListResponse::new)
                .collect(Collectors.toUnmodifiableList());

        return new ResultList(books.size(), books);
    }

    @GetMapping("/{bookId}")
    public BookResponse get(@CurrentUser Long userId, @PathVariable Long bookId) {
        Book book = bookService.get(userId, bookId);
        return new BookResponse(book);
    }

    @PostMapping
    public void create(@CurrentUser Long userId, @RequestBody @Valid BookCreate bookCreate) {
        bookService.create(userId, bookCreate);
    }

    @PostMapping("/{bookId}")
    public void update(@CurrentUser Long userId, @PathVariable Long bookId, @RequestBody @Valid BookUpdate bookUpdate) {
        bookService.update(userId, bookId, bookUpdate);
    }

    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@CurrentUser Long userId, @PathVariable Long bookId) {
        bookService.delete(userId, bookId);
    }
}
