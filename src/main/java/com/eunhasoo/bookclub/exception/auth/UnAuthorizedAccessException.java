package com.eunhasoo.bookclub.exception.auth;

import com.eunhasoo.bookclub.exception.BookClubException;

public class UnAuthorizedAccessException extends BookClubException {

    private static final String MESSAGE = "해당 요청을 위한 접근 권한이 없습니다.";

    public UnAuthorizedAccessException(String url) {
        super(String.format("%s -> [Request URL: %s]", MESSAGE, url));
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
