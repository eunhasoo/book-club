package com.eunhasoo.bookclub.book.ui.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class BookshelfUpdate {

    @NotBlank(message = "변경하고자 하는 책장의 이름을 입력해주세요.")
    @Size(max = 20, message = "책장 이름은 20자 이내로만 가능합니다.")
    private String name;

    @NotNull(message = "공개 여부를 선택해주세요.")
    private Boolean isPublic;

    private BookshelfUpdate() {
    }

    public BookshelfUpdate(String name, Boolean isPublic) {
        this.name = name;
        this.isPublic = isPublic;
    }
}
