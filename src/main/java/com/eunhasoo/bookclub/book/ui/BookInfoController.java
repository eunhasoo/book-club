package com.eunhasoo.bookclub.book.ui;

import com.eunhasoo.bookclub.book.application.BookInfoService;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.ui.request.BookInfoCreate;
import com.eunhasoo.bookclub.book.ui.request.BookInfoSearch;
import com.eunhasoo.bookclub.book.ui.response.BookInfoCreateResponse;
import com.eunhasoo.bookclub.book.ui.response.BookInfoFindResponse;
import com.eunhasoo.bookclub.book.ui.response.BookInfoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/bookinfo")
public class BookInfoController {

    private final BookInfoService bookInfoService;

    public BookInfoController(BookInfoService bookInfoService) {
        this.bookInfoService = bookInfoService;
    }

    @GetMapping("/search")
    public BookInfoFindResponse find(@ModelAttribute @Valid BookInfoSearch bookInfoSearch) {
        Long id = bookInfoService.getIdByIsbn(bookInfoSearch.getIsbn());
        return new BookInfoFindResponse(id);
    }

    @GetMapping("/{bookInfoId}")
    public BookInfoResponse get(@PathVariable Long bookInfoId) {
        BookInfo bookInfo = bookInfoService.get(bookInfoId);
        return new BookInfoResponse(bookInfo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookInfoCreateResponse create(@RequestBody @Valid BookInfoCreate bookInfoCreate) {
        Long id = bookInfoService.create(bookInfoCreate);
        return new BookInfoCreateResponse(id);
    }
}
