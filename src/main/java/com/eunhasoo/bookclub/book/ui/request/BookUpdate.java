package com.eunhasoo.bookclub.book.ui.request;

import com.eunhasoo.bookclub.book.domain.ReadProcess;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class BookUpdate {

    @NotNull
    private ReadProcess readProcess;

    private BookUpdate() {
    }

    public BookUpdate(ReadProcess readProcess) {
        this.readProcess = readProcess;
    }
}
