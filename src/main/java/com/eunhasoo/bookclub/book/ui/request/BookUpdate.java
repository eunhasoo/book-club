package com.eunhasoo.bookclub.book.ui.request;

import com.eunhasoo.bookclub.book.domain.ReadProcess;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class BookUpdate {

    @NotNull(message = "현재 읽기 상태를 선택해주세요.")
    private ReadProcess readProcess;

    private BookUpdate() {
    }

    public BookUpdate(ReadProcess readProcess) {
        this.readProcess = readProcess;
    }
}
