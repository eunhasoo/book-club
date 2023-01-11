package com.eunhasoo.bookclub.book.ui.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class BookInfoSearch {

    @NotBlank(message = "찾을 책의 ISBN을 13자로 입력해주세요.")
    @Size(min = 13, max = 13, message = "ISBN이 13자로 입력되었는지 다시 한번 확인해주세요.")
    private String isbn;

    private BookInfoSearch() {
    }

    public BookInfoSearch(String isbn) {
        this.isbn = isbn;
    }
}
