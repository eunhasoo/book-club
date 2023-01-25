package com.eunhasoo.bookclub.book.ui.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class BookshelfUpdate {

    @NotBlank(message = "변경하고자 하는 책장의 이름을 입력해주세요.")
    private String name;

    @NotNull
    private Boolean isPublic;

    private BookshelfUpdate() {
    }

    public BookshelfUpdate(String name, Boolean isPublic) {
        this.name = name;
        this.isPublic = isPublic;
    }
}
