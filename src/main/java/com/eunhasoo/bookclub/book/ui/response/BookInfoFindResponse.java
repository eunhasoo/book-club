package com.eunhasoo.bookclub.book.ui.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class BookInfoFindResponse {
    private boolean isExist;
    private Long bookInfoId;

    @JsonProperty(value="isExist")
    public boolean isExist() {
        return isExist;
    }

    public Long getBookInfoId() {
        return bookInfoId;
    }

    @Builder
    public BookInfoFindResponse(Long bookInfoId) {
        this.isExist = bookInfoId != null;
        this.bookInfoId = bookInfoId;
    }
}
