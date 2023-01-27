package com.eunhasoo.bookclub.book.ui.request;

import com.eunhasoo.bookclub.book.domain.ReadProcess;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class BookCreate {

    @NotNull
    private long bookInfoId;

    @NotNull
    private long bookShelfId;

    private ReadProcess readProcess;

    private BookCreate() {
    }

    public BookCreate(long bookInfoId, long bookShelfId, ReadProcess readProcess) {
        this.bookInfoId = bookInfoId;
        this.bookShelfId = bookShelfId;
        this.readProcess = readProcess;
    }
}
