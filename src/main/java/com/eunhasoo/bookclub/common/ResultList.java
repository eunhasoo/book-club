package com.eunhasoo.bookclub.common;

import lombok.Getter;

@Getter
public class ResultList<T> {
    private int count;
    private T data;

    public ResultList(int count, T data) {
        this.count = count;
        this.data = data;
    }
}
