package com.eunhasoo.bookclub.exception.auth;

import com.eunhasoo.bookclub.exception.BookClubException;

public class InvalidTokenException extends BookClubException {

    private static final String MESSAGE = "토큰 내부 정보가 잘못되었습니다.";

    public InvalidTokenException(String subject) {
        super(String.format("%s -> [subject: %s]", MESSAGE, subject));
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
