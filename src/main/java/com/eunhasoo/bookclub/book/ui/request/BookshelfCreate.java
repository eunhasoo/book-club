package com.eunhasoo.bookclub.book.ui.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class BookshelfCreate {

    @NotBlank(message = "책장 이름을 입력해주세요.")
    private String name;

    private boolean isOpen;

    private BookshelfCreate() {
    }

    public BookshelfCreate(String name, boolean isOpen) {
        this.name = name;
        this.isOpen = isOpen;
    }

    public String getName() {
        return name;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
