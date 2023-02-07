package com.eunhasoo.bookclub.book.ui.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class BookshelfCreate {

    @NotBlank(message = "책장 이름을 입력해주세요.")
    @Size(max = 20, message = "책장 이름은 20자 이내로만 가능합니다.")
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
